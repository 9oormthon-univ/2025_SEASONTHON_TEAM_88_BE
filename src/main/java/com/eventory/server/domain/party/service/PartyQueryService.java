package com.eventory.server.domain.party.service;

import com.eventory.server.domain.common.dto.GeminiRequestDTO;
import com.eventory.server.domain.common.dto.GeminiResponseDTO;
import com.eventory.server.domain.like.entity.Like;
import com.eventory.server.domain.like.repository.LikeRepository;
import com.eventory.server.domain.member.entity.Member;
import com.eventory.server.domain.member.repository.MemberRepository;
import com.eventory.server.domain.party.dto.PartyRequestDTO;
import com.eventory.server.domain.party.dto.PartyResponseDTO;
import com.eventory.server.domain.party.entity.enums.ExpectedRange;
import com.eventory.server.domain.product.entity.Product;
import com.eventory.server.domain.product.repository.ProductRepository;
import com.eventory.server.global.apipayload.code.status.ErrorStatus;
import com.eventory.server.global.apipayload.exception.handler.MemberHandler;
import com.eventory.server.global.apipayload.exception.handler.PartyHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.eventory.server.domain.party.convter.PartyConverter.toProductInfo;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PartyQueryService {

    private final MemberRepository memberRepository;
    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${gemini.prompts.similarItemPrompt}")
    private String getSimilarItemPrompt;

    @Value("${gemini.prompts.similarItemPrompt}")
    private String createPackagePrompt;

    private final ProductRepository productRepository;
    private final LikeRepository likeRepository;

    public List<PartyResponseDTO.ProductInfo> getSimilarItem(Long memberId){

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        List<String> productNameList = likeRepository.findByMemberWithProduct(member).stream()
                .map(Like::getProduct)
                .map(Product::getProductName)
                .collect(Collectors.toList());

        List<Product> similarProductList = getProductsSimilarToLiked(productNameList);

        return similarProductList.stream()
                .sorted(Comparator.comparing(Product::getPrice))
                .limit(5)
                .map(product -> toProductInfo(product))
                .collect(Collectors.toList());
    }

    private List<Product> getProductsSimilarToLiked(List<String> productNameList) {

        String geminiURL = geminiApiUrl + "?key=" + geminiApiKey;
        String preparationContent = String.join(", ", productNameList);

        String prompt = getSimilarItemPrompt + preparationContent;

        GeminiRequestDTO requestDto = GeminiRequestDTO.builder()
                .contents(List.of(
                        GeminiRequestDTO.Content.builder()
                                .parts(List.of(
                                        GeminiRequestDTO.Part.builder()
                                                .text(prompt)
                                                .build()
                                ))
                                .build()
                ))
                .build();

        WebClient webClient = WebClient.builder().build();

        try {
            GeminiResponseDTO response = webClient.post()
                    .uri(geminiURL)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestDto)
                    .retrieve()
                    .bodyToMono(GeminiResponseDTO.class)
                    .block();

            String jsonResponse = response.getCandidates().get(0).getContent().getParts().get(0).getText();

            try {
                String cleanJsonResponse = jsonResponse;
                if (jsonResponse.contains("```")) {
                    cleanJsonResponse = jsonResponse
                            .replaceAll("```json\\s*", "")
                            .replaceAll("```\\s*", "")
                            .trim();
                }

                ObjectMapper mapper = new ObjectMapper();
                JsonNode node = mapper.readTree(cleanJsonResponse);
                JsonNode keywordsArray = node.get("keywords");

                List<String> keywords = new ArrayList<>();
                if (keywordsArray != null && keywordsArray.isArray()) {
                    for (JsonNode keyword : keywordsArray) {
                        keywords.add(keyword.asText());
                    }
                }

                List<Product> products = new ArrayList<>();
                for (String keyword : keywords) {
                    List<Product> found = productRepository.findByProductNameContaining(keyword);
                    products.addAll(found);
                }

                return products.stream()
                        .distinct()
                        .collect(Collectors.toList());

            } catch (Exception jsonException) {
                String[] words = preparationContent.split("[,\\s]+");
                List<String> fallbackKeywords = new ArrayList<>();
                for (String word : words) {
                    if (word.length() >= 2) {
                        fallbackKeywords.add(word.trim());
                    }
                }

                List<Product> products = new ArrayList<>();
                for (String keyword : fallbackKeywords) {
                    List<Product> found = productRepository.findByProductNameContaining(keyword);
                    products.addAll(found);
                }
                return products.stream()
                        .distinct()
                        .collect(Collectors.toList());
            }

        } catch (Exception e) {
            throw new PartyHandler(ErrorStatus.GEMINI_NOT_WORK);
        }
    }

    // AI 키워드 추출
    private List<String> extractKeywordsFromGemini(String preparationContent) {

        String geminiURL = geminiApiUrl + "?key=" + geminiApiKey;

        String prompt = createPackagePrompt + preparationContent;

        GeminiRequestDTO requestDto = GeminiRequestDTO.builder()
                .contents(List.of(
                        GeminiRequestDTO.Content.builder()
                                .parts(List.of(
                                        GeminiRequestDTO.Part.builder()
                                                .text(prompt)
                                                .build()
                                ))
                                .build()
                ))
                .build();

        WebClient webClient = WebClient.builder().build();
        try {
            GeminiResponseDTO response = webClient.post()
                    .uri(geminiURL)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestDto)
                    .retrieve()
                    .bodyToMono(GeminiResponseDTO.class)
                    .block();

            String jsonResponse = response.getCandidates().get(0).getContent().getParts().get(0).getText();

            System.out.println("=== Gemini API 응답 ===");
            System.out.println("Input: " + preparationContent);
            System.out.println("Response: " + jsonResponse);
            System.out.println("====================");

            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode node = mapper.readTree(jsonResponse);
                JsonNode keywordsArray = node.get("keywords");

                List<String> keywords = new ArrayList<>();
                if (keywordsArray != null && keywordsArray.isArray()) {
                    for (JsonNode keyword : keywordsArray) {
                        keywords.add(keyword.asText());
                    }
                }
                return keywords;
            } catch (Exception jsonException) {
                return extractKeywordsSimple(preparationContent);
            }

        } catch (Exception e) {
            throw new PartyHandler(ErrorStatus.GEMINI_NOT_WORK);
        }
    }

    // 설문 기반 맞춤 패키지 생성
    public PartyResponseDTO.CustomPackageResponse createParty(PartyRequestDTO.PartySurveyRequest request){

        List<Product> filteredProducts = productRepository.findByPurposeAndParticipantTypeAndCategoryIn(
                request.getPartyPurpose(),
                request.getCompanionType(),
                request.getWishListItems()
        );

        if (request.getPreparationContent() != null && !request.getPreparationContent().trim().isEmpty()) {
            filteredProducts = toGemini(filteredProducts, request.getPreparationContent());
        }

        List<PartyResponseDTO.BudgetPackage> budgetPackages = createBudgetPackages(filteredProducts, request.getBudgetRange());

        return PartyResponseDTO.CustomPackageResponse.builder()
                .customPackage(budgetPackages)
                .build();
    }

    // AI 키워드 기반 상품 필터링
    private List<Product> toGemini(List<Product> products, String preparationContent) {
        if (preparationContent == null || preparationContent.trim().isEmpty()) {
            return products;
        }

        List<String> keywords = extractKeywordsFromGemini(preparationContent);
        if (keywords.isEmpty()) {
            return products;
        }

        List<Product> keywordProducts = new ArrayList<>();
        for (String keyword : keywords) {
            List<Product> foundProducts = productRepository.findByProductNameContaining(keyword);
            keywordProducts.addAll(foundProducts);
        }

        Set<Long> productIds = products.stream().map(Product::getId).collect(Collectors.toSet());
        for (Product keywordProduct : keywordProducts) {
            if (!productIds.contains(keywordProduct.getId())) {
                products.add(keywordProduct);
                productIds.add(keywordProduct.getId());
            }
        }

        return products;
    }

    // simple 키워드 추출
    private List<String> extractKeywordsSimple(String content) {
        List<String> keywords = new ArrayList<>();
        String[] commonKeywords
                = {"귀여운", "예쁜", "핑크", "파란", "빨간", "케이크", "꽃", "풍선", "초", "선물", "카드", "리본", "장식", "파티", "생일", "러블리", "컨셉",
                "로맨틱", "프로포즈", "결혼", "반지", "다이아몬드", "장미", "빨간장미", "화이트", "하얀", "순수", "우아한", "고급", "엘레간트",
                "촛불", "캔들", "분위기", "특별한", "감동", "사랑", "하트", "로즈", "샴페인", "와인", "미식", "고급스러운", "클래식", "모던",
                "빈티지", "골드", "실버", "진주", "크리스털", "벨벳", "새틴", "레이스", "투명", "글리터", "반짝", "은은한", "부드러운"};

        for (String keyword : commonKeywords) {
            if (content.contains(keyword)) {
                keywords.add(keyword);
            }
        }
        return keywords;
    }

    // 예산별 패키지 생성
    private List<PartyResponseDTO.BudgetPackage> createBudgetPackages(List<Product> products, ExpectedRange budgetRange) {
        List<PartyResponseDTO.BudgetPackage> packages = new ArrayList<>();

        int[] budgets = getBudgetRangeValues(budgetRange);
        for (int budget : budgets) {
            packages.add(createPackageForBudget(products, budget));
        }

        return packages;
    }

    // 예산 범위 값 반환
    private int[] getBudgetRangeValues(ExpectedRange range) {
        return switch (range) {
            case UNDER_10000 -> new int[]{10000};
            case FROM_10000_TO_20000 -> new int[]{10000, 20000};
            case FROM_30000_TO_40000 -> new int[]{30000, 40000};
            case FROM_50000_TO_60000 -> new int[]{50000, 60000};
            case FROM_70000_TO_80000 -> new int[]{70000, 80000};
            case FROM_90000_TO_100000 -> new int[]{90000, 100000};
            case OVER_100000 -> new int[]{100000, 120000, 150000};
            case OTHER -> new int[]{50000, 70000};
        };
    }

    // 특정 예산에 맞는 패키지 생성
    private PartyResponseDTO.BudgetPackage createPackageForBudget(List<Product> products, int budget) {
        List<PartyResponseDTO.ProductInfo> productInfos = new ArrayList<>();

        if (products.size() >= 3) {
            List<Product> sortedProducts = products.stream()
                    .sorted((p1, p2) -> Integer.compare(p1.getPrice(), p2.getPrice()))
                    .collect(Collectors.toList());

            List<Product> bestCombination = findBestCombination(sortedProducts, budget);

            if (bestCombination.size() < 3) {
                bestCombination = sortedProducts.stream()
                        .limit(3)
                        .collect(Collectors.toList());
            }

            for (Product product : bestCombination) {
                productInfos.add(PartyResponseDTO.ProductInfo.builder()
                        .productImage(product.getProductImages().isEmpty() ?
                                "https://goorm-eventory-bucket.s3.ap-northeast-2.amazonaws.com/product/Frame+1321318195+(1).png"
                                : product.getProductImages().get(0).getImageUrl())
                        .productName(product.getProductName())
                        .productId(product.getId())
                        .category(product.getCategory())
                        .price(product.getPrice())
                        .build());
            }
        }

        return PartyResponseDTO.BudgetPackage.builder()
                .budget(budget / 10000)
                .products(productInfos)
                .build();
    }

    // 최적 조합 찾기
    private List<Product> findBestCombination(List<Product> sortedProducts, int budget) {
        List<Product> bestCombination = new ArrayList<>();
        int currentTotal = 0;

        int minBudget = budget;
        int maxBudget = budget + 9999;

        for (int i = 0; i < sortedProducts.size() - 2; i++) {
            for (int j = i + 1; j < sortedProducts.size() - 1; j++) {
                for (int k = j + 1; k < sortedProducts.size(); k++) {
                    int total = sortedProducts.get(i).getPrice() +
                            sortedProducts.get(j).getPrice() +
                            sortedProducts.get(k).getPrice();

                    if (total >= minBudget && total <= maxBudget && total > currentTotal) {
                        bestCombination.clear();
                        bestCombination.add(sortedProducts.get(i));
                        bestCombination.add(sortedProducts.get(j));
                        bestCombination.add(sortedProducts.get(k));
                        currentTotal = total;
                    }
                }
            }
        }

        if (bestCombination.isEmpty()) {
            currentTotal = 0;
            for (int i = 0; i < sortedProducts.size() - 2; i++) {
                for (int j = i + 1; j < sortedProducts.size() - 1; j++) {
                    for (int k = j + 1; k < sortedProducts.size(); k++) {
                        int total = sortedProducts.get(i).getPrice() +
                                sortedProducts.get(j).getPrice() +
                                sortedProducts.get(k).getPrice();

                        if (total <= budget && total > currentTotal) {
                            bestCombination.clear();
                            bestCombination.add(sortedProducts.get(i));
                            bestCombination.add(sortedProducts.get(j));
                            bestCombination.add(sortedProducts.get(k));
                            currentTotal = total;
                        }
                    }
                }
            }
        }
        return bestCombination;
    }
}
