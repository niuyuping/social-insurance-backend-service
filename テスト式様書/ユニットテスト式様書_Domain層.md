# ユニットテスト式様書（Unit Test Specification）- Domain層

## 1. テスト概要

### 1.1 テスト対象システム
**システム名**: 社会保险バックエンドサービス（Social Insurance Backend Service）  
**テスト対象クラス**: `PremiumBracketDomainService`  
**テスト種別**: ユニットテスト（Unit Test）  
**テスト実施日**: -  
**テスト実施者**: -  
**バージョン**: 0.0.1

### 1.2 テスト環境
- **フレームワーク**: Spring Boot 4.0.0 (WebFlux)
- **テストツール**: JUnit 5, Mockito, StepVerifier, AssertJ
- **Java バージョン**: 21
- **テストアノテーション**: `@SpringBootTest(classes = PremiumBracketDomainService.class)`

### 1.3 テスト範囲
本テスト式様書は、Domain層のユニットテストを対象とする。  
ユニットテストでは、以下の要素をテストする：
- Domain層のビジネスロジック（社会保险金額計算）
- Repository層へのデータアクセス
- 40歳以上/未満による介護保険の適用判定
- 例外処理（異常系）

### 1.4 ユニットテストの特徴
- `@SpringBootTest(classes = ...)`により、特定のクラスのみをロード
- `@MockitoBean`により、Repository層の依存関係をモック化
- `StepVerifier`により、Reactive Streamの検証を行う
- 実際のデータベースは使用しない

---

## 2. テスト項目一覧

| No. | テスト項目ID | テスト項目名 | テスト種別 | 優先度 |
|-----|------------|------------|----------|--------|
| 1 | TC-D-001 | 社会保险金額照会（40歳以上・月給55万円） | 正常系 | 高 |
| 2 | TC-D-002 | 社会保险金額照会（40歳未満・月給28万円） | 正常系 | 高 |
| 3 | TC-D-003 | 社会保险金額照会（存在しない月給・異常系） | 異常系 | 高 |

---

## 3. テスト項目詳細

### 3.1 TC-D-001: 社会保险金額照会（40歳以上・月給55万円）

#### 3.1.1 テスト目的
40歳以上の従業員（月給55万円）に対する社会保险金額計算が正しく行われることを確認する。  
特に、介護保険が適用されることと、従業員負担額・事業主負担額の計算が正しいことを確認する。

#### 3.1.2 前提条件
- `PremiumBracketRepository`がモック化されていること
- `PremiumBracketDomainService`が正しく初期化されていること

#### 3.1.3 テストデータ
| 項目 | 値 |
|-----|-----|
| 月給（monthlySalary） | 550,000円 |
| 年齢（age） | 45歳 |

#### 3.1.4 モック設定
`PremiumBracketRepository.findByAmount(550000)`が以下の`PremiumBracket`エンティティを返すように設定：
- 等級: `"32(29)"`
- 标准报酬: 560,000円
- 最小値: 545,000円
- 最大値: 575,000円
- 健康保険料（介護なし）: 55,552.00円
- 健康保険料（介護あり）: 64,456.00円
- 厚生年金保険料: 102,480.00円

#### 3.1.5 テスト手順
1. `PremiumBracketRepository`のモックを設定する
2. `PremiumBracketDomainService.socialInsuranceQuery(550000, 45)`を呼び出す
3. `StepVerifier`を使用してReactive Streamを検証する
4. レスポンスの各項目を検証する

#### 3.1.6 期待結果

##### 3.1.6.1 戻り値（従業員負担額）
| 項目 | 期待値 | 単位 | 計算根拠 |
|-----|--------|------|---------|
| 健康保険料（介護なし） | 27,776.00 | 円 | 55,552.00 ÷ 2 |
| 介護保険料 | 4,452.00 | 円 | (64,456.00 - 55,552.00) ÷ 2 |
| 厚生年金保険料 | 51,240.00 | 円 | 102,480.00 ÷ 2 |

##### 3.1.6.2 戻り値（事業主負担額）
| 項目 | 期待値 | 単位 | 計算根拠 |
|-----|--------|------|---------|
| 健康保険料（介護なし） | 27,776.00 | 円 | 55,552.00 ÷ 2 |
| 介護保険料 | 4,452.00 | 円 | (64,456.00 - 55,552.00) ÷ 2 |
| 厚生年金保険料 | 51,240.00 | 円 | 102,480.00 ÷ 2 |

#### 3.1.7 判定基準
- Reactive Streamが正常に完了すること（`verifyComplete()`）
- レスポンスの各項目が期待値と一致すること
- 介護保険料が0円ではないこと（40歳以上であるため）
- `PremiumBracketRepository.findByAmount`が1回呼び出されること
- 従業員負担額と事業主負担額が一致すること（このケースでは同額）

#### 3.1.8 備考
- 40歳以上の従業員は介護保険の対象となるため、`careCost`が0円以外の値となる
- 金額は`BigDecimal`型で比較し、精度を保証する
- このテストはDomain層のビジネスロジック（計算処理）を検証する

---

### 3.2 TC-D-002: 社会保险金額照会（40歳未満・月給28万円）

#### 3.2.1 テスト目的
40歳未満の従業員（月給28万円）に対する社会保险金額計算が正しく行われることを確認する。  
特に、介護保険が適用されないことを確認する。

#### 3.2.2 前提条件
- `PremiumBracketRepository`がモック化されていること
- `PremiumBracketDomainService`が正しく初期化されていること

#### 3.2.3 テストデータ
| 項目 | 値 |
|-----|-----|
| 月給（monthlySalary） | 280,000円 |
| 年齢（age） | 25歳 |

#### 3.2.4 モック設定
`PremiumBracketRepository.findByAmount(280000)`が以下の`PremiumBracket`エンティティを返すように設定：
- 等級: `"21(18)"`
- 标准报酬: 280,000円
- 最小値: 270,000円
- 最大値: 290,000円
- 健康保険料（介護なし）: 27,776.00円
- 健康保険料（介護あり）: 32,228.00円
- 厚生年金保険料: 51,240.00円

#### 3.2.5 テスト手順
1. `PremiumBracketRepository`のモックを設定する
2. `PremiumBracketDomainService.socialInsuranceQuery(280000, 25)`を呼び出す
3. `StepVerifier`を使用してReactive Streamを検証する
4. レスポンスの各項目を検証する

#### 3.2.6 期待結果

##### 3.2.6.1 戻り値（従業員負担額）
| 項目 | 期待値 | 単位 | 計算根拠 |
|-----|--------|------|---------|
| 健康保険料（介護なし） | 13,888.00 | 円 | 27,776.00 ÷ 2 |
| 介護保険料 | 0.00 | 円 | 40歳未満のため適用なし |
| 厚生年金保険料 | 25,620.00 | 円 | 51,240.00 ÷ 2 |

##### 3.2.6.2 戻り値（事業主負担額）
| 項目 | 期待値 | 単位 | 計算根拠 |
|-----|--------|------|---------|
| 健康保険料（介護なし） | 13,888.00 | 円 | 27,776.00 ÷ 2 |
| 介護保険料 | 0.00 | 円 | 40歳未満のため適用なし |
| 厚生年金保険料 | 25,620.00 | 円 | 51,240.00 ÷ 2 |

#### 3.2.7 判定基準
- Reactive Streamが正常に完了すること（`verifyComplete()`）
- レスポンスの各項目が期待値と一致すること
- 介護保険料が0.00円であること（40歳未満であるため）
- `PremiumBracketRepository.findByAmount`が1回呼び出されること

#### 3.2.8 備考
- 40歳未満の従業員は介護保険の対象外となるため、`careCost`が0.00円となる
- このテストは年齢による介護保険の適用判定ロジックを検証する

---

### 3.3 TC-D-003: 社会保险金額照会（存在しない月給・異常系）

#### 3.3.1 テスト目的
存在しない月給を指定した場合、適切な例外がスローされることを確認する。

#### 3.3.2 前提条件
- `PremiumBracketRepository`がモック化されていること
- `PremiumBracketDomainService`が正しく初期化されていること

#### 3.3.3 テストデータ
| 項目 | 値 |
|-----|-----|
| 月給（monthlySalary） | 1,050,000円（存在しない値） |
| 年齢（age） | 30歳 |

#### 3.3.4 モック設定
`PremiumBracketRepository.findByAmount(1050000)`が`Mono.empty()`を返すように設定（データが見つからない）

#### 3.3.5 テスト手順
1. `PremiumBracketRepository`のモックを設定する（空の結果を返すように）
2. `PremiumBracketDomainService.socialInsuranceQuery(1050000, 30)`を呼び出す
3. `StepVerifier`を使用して例外を検証する

#### 3.3.6 期待結果

##### 3.3.6.1 例外
- **例外タイプ**: `IllegalArgumentException`
- **エラーメッセージ**: `"未找到月薪 1050000 对应的保险费等级"`を含む

#### 3.3.7 判定基準
- `IllegalArgumentException`がスローされること
- エラーメッセージが期待値と一致すること
- `PremiumBracketRepository.findByAmount`が1回呼び出されること
- Reactive Streamがエラーで終了すること（`expectErrorMatches`）

#### 3.3.8 備考
- このテストは異常系のユニットテストである
- Repository層から空の結果が返された場合、Domain層で適切に例外をスローすることを確認する
- `switchIfEmpty`を使用して`Mono.empty()`を`Mono.error()`に変換するロジックを検証する

---

## 4. テスト実行方法

### 4.1 テスト実行コマンド
```bash
./gradlew test
```

### 4.2 特定のテストクラスのみ実行
```bash
./gradlew test --tests SocialInsuranceDomainServiceTest
```

### 4.3 特定のテストメソッドのみ実行
```bash
./gradlew test --tests SocialInsuranceDomainServiceTest.testSocialInsuranceDomainService_AgeOver40
```

---

## 5. テスト環境セットアップ

### 5.1 必要な環境
- Java 21以上
- Gradle

### 5.2 テストアノテーション
- `@SpringBootTest(classes = PremiumBracketDomainService.class)`: 特定のクラスのみをロード
- `@MockitoBean`: Repository層の依存関係をモック化
- `@Autowired`: Domain層のサービスを自動注入

---

## 6. テスト結果記録

### 6.1 テスト実行結果サマリー
| テスト項目ID | テスト項目名 | 実行日時 | 結果 | 備考 |
|------------|------------|---------|------|------|
| TC-D-001 | 社会保险金額照会（40歳以上・月給55万円） | - | - | - |
| TC-D-002 | 社会保险金額照会（40歳未満・月給28万円） | - | - | - |
| TC-D-003 | 社会保险金額照会（存在しない月給・異常系） | - | - | - |

### 6.2 不具合管理
不具合が発見された場合、以下の情報を記録する：
- 不具合ID
- 発見日時
- 再現手順
- 期待結果と実際の結果
- 優先度・重要度

---

## 7. 参考情報

### 7.1 テスト対象クラス
- **Domain層**: `src/main/java/jp/asatex/niuyuping/social_insurance_backend_service/domain/PremiumBracketDomainService.java`
- **テストクラス**: `src/test/java/jp/asatex/niuyuping/social_insurance_backend_service/domain/SocialInsuranceDomainServiceTest.java`

### 7.2 関連クラス
- Repository層: `PremiumBracketRepository`, `PremiumBracketRepositoryImpl`
- Entity: `PremiumBracket`
- DTO: `SocialInsuranceDomainDto`

---

## 8. 改訂履歴

| 版数 | 改訂日 | 改訂内容 | 改訂者 |
|-----|--------|---------|--------|
| 1.0 | - | 初版作成（Domain層ユニットテスト式様書として作成） | - |

---

**文書作成日**: -  
**最終更新日**: -  
**承認者**: -

