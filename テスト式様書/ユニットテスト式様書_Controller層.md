# ユニットテスト式様書（Unit Test Specification）- Controller層

## 1. テスト概要

### 1.1 テスト対象システム
**システム名**: 社会保险バックエンドサービス（Social Insurance Backend Service）  
**テスト対象クラス**: `PremiumBracketController`  
**テスト種別**: ユニットテスト（Unit Test）  
**テスト実施日**: -  
**テスト実施者**: -  
**バージョン**: 0.0.1

### 1.2 テスト環境
- **フレームワーク**: Spring Boot 4.0.0 (WebFlux)
- **テストツール**: JUnit 5, WebTestClient, Mockito, AssertJ
- **Java バージョン**: 21
- **テストアノテーション**: `@WebFluxTest`

### 1.3 テスト範囲
本テスト式様書は、Controller層のユニットテストを対象とする。  
ユニットテストでは、以下の要素をテストする：
- HTTPリクエスト/レスポンス処理（Controller層）
- パラメータバリデーション
- DTO変換処理
- 例外処理（異常系）

### 1.4 ユニットテストの特徴
- `@WebFluxTest`により、Controller層のみをスライスしてテスト
- `@MockitoBean`により、Application層の依存関係をモック化
- 実際のデータベースやビジネスロジックは使用しない
- 高速に実行可能

---

## 2. テスト項目一覧

| No. | テスト項目ID | テスト項目名 | テスト種別 | 優先度 |
|-----|------------|------------|----------|--------|
| 1 | TC-C-001 | 社会保险金額照会（40歳以上・月給55万円） | 正常系 | 高 |
| 2 | TC-C-002 | 社会保险金額照会（40歳未満・月給28万円） | 正常系 | 高 |
| 3 | TC-C-003 | 社会保险金額照会（存在しない月給・異常系） | 異常系 | 高 |

---

## 3. テスト項目詳細

### 3.1 TC-C-001: 社会保险金額照会（40歳以上・月給55万円）

#### 3.1.1 テスト目的
40歳以上の従業員（月給55万円）に対する社会保险金額照会APIが正しく動作することを確認する。  
特に、Application層のモックが正しく呼び出され、DTO変換が正しく行われることを確認する。

#### 3.1.2 前提条件
- `PremiumBracketApplicationService`がモック化されていること
- WebTestClientが正しく初期化されていること

#### 3.1.3 テストデータ
| 項目 | 値 |
|-----|-----|
| 月給（monthlySalary） | 550,000円 |
| 年齢（age） | 45歳 |

#### 3.1.4 モック設定
`PremiumBracketApplicationService.socialInsuranceQuery(550000, 45)`が以下の値を返すように設定：
- 従業員負担額: 健康保険料27,776.00円、介護保険料4,452.00円、厚生年金51,240.00円
- 事業主負担額: 健康保険料27,776.00円、介護保険料4,452.00円、厚生年金51,240.00円

#### 3.1.5 テスト手順
1. `PremiumBracketApplicationService`のモックを設定する
2. HTTP GETリクエストを送信する
   - URL: `/socialInsuranceQuery?monthlySalary=550000&age=45`
   - メソッド: GET
3. レスポンスを取得する
4. レスポンスの各項目を検証する

#### 3.1.6 期待結果

##### 3.1.6.1 HTTPレスポンス
- **ステータスコード**: 200 OK
- **Content-Type**: `application/json`

##### 3.1.6.2 レスポンスボディ（従業員負担額）
| 項目 | 期待値 | 単位 |
|-----|--------|------|
| 健康保険料（介護なし） | 27,776.00 | 円 |
| 介護保険料 | 4,452.00 | 円 |
| 厚生年金保険料 | 51,240.00 | 円 |

##### 3.1.6.3 レスポンスボディ（事業主負担額）
| 項目 | 期待値 | 単位 |
|-----|--------|------|
| 健康保険料（介護なし） | 27,776.00 | 円 |
| 介護保険料 | 4,452.00 | 円 |
| 厚生年金保険料 | 51,240.00 | 円 |

#### 3.1.7 判定基準
- HTTPステータスコードが200 OKであること
- レスポンスボディの各項目が期待値と一致すること
- `PremiumBracketApplicationService.socialInsuranceQuery`が1回呼び出されること
- DTO変換が正しく行われること

#### 3.1.8 備考
- Application層のロジックはテスト対象外（モック化されている）
- このテストはController層のHTTP処理とDTO変換のみを検証する

---

### 3.2 TC-C-002: 社会保险金額照会（40歳未満・月給28万円）

#### 3.2.1 テスト目的
40歳未満の従業員（月給28万円）に対する社会保险金額照会APIが正しく動作することを確認する。  
特に、介護保険が適用されない場合の処理を確認する。

#### 3.2.2 前提条件
- `PremiumBracketApplicationService`がモック化されていること
- WebTestClientが正しく初期化されていること

#### 3.2.3 テストデータ
| 項目 | 値 |
|-----|-----|
| 月給（monthlySalary） | 280,000円 |
| 年齢（age） | 25歳 |

#### 3.2.4 モック設定
`PremiumBracketApplicationService.socialInsuranceQuery(280000, 25)`が以下の値を返すように設定：
- 従業員負担額: 健康保険料13,888.00円、介護保険料0.00円、厚生年金25,620.00円
- 事業主負担額: 健康保険料13,888.00円、介護保険料0.00円、厚生年金25,620.00円

#### 3.2.5 テスト手順
1. `PremiumBracketApplicationService`のモックを設定する
2. HTTP GETリクエストを送信する
   - URL: `/socialInsuranceQuery?monthlySalary=280000&age=25`
   - メソッド: GET
3. レスポンスを取得する
4. レスポンスの各項目を検証する

#### 3.2.6 期待結果

##### 3.2.6.1 HTTPレスポンス
- **ステータスコード**: 200 OK
- **Content-Type**: `application/json`

##### 3.2.6.2 レスポンスボディ（従業員負担額）
| 項目 | 期待値 | 単位 |
|-----|--------|------|
| 健康保険料（介護なし） | 13,888.00 | 円 |
| 介護保険料 | 0.00 | 円 |
| 厚生年金保険料 | 25,620.00 | 円 |

##### 3.2.6.3 レスポンスボディ（事業主負担額）
| 項目 | 期待値 | 単位 |
|-----|--------|------|
| 健康保険料（介護なし） | 13,888.00 | 円 |
| 介護保険料 | 0.00 | 円 |
| 厚生年金保険料 | 25,620.00 | 円 |

#### 3.2.7 判定基準
- HTTPステータスコードが200 OKであること
- レスポンスボディの各項目が期待値と一致すること
- 介護保険料が0.00円であること（40歳未満であるため）
- `PremiumBracketApplicationService.socialInsuranceQuery`が1回呼び出されること

#### 3.2.8 備考
- 介護保険料が0.00円であることを確認することで、DTO変換が正しく行われていることを検証する

---

### 3.3 TC-C-003: 社会保险金額照会（存在しない月給・異常系）

#### 3.3.1 テスト目的
存在しない月給を指定した場合、適切なエラーレスポンスが返されることを確認する。

#### 3.3.2 前提条件
- `PremiumBracketApplicationService`がモック化されていること
- WebTestClientが正しく初期化されていること

#### 3.3.3 テストデータ
| 項目 | 値 |
|-----|-----|
| 月給（monthlySalary） | 1,050,000円（存在しない値） |
| 年齢（age） | 30歳 |

#### 3.3.4 モック設定
`PremiumBracketApplicationService.socialInsuranceQuery(1050000, 30)`が`IllegalArgumentException`をスローするように設定：
- エラーメッセージ: `"未找到月薪 1050000 对应的保险费等级"`

#### 3.3.5 テスト手順
1. `PremiumBracketApplicationService`のモックを設定する（エラーを返すように）
2. HTTP GETリクエストを送信する
   - URL: `/socialInsuranceQuery?monthlySalary=1050000&age=30`
   - メソッド: GET
3. レスポンスを取得する
4. エラーレスポンスの内容を検証する

#### 3.3.6 期待結果

##### 3.3.6.1 HTTPレスポンス
- **ステータスコード**: 400 Bad Request
- **Content-Type**: `application/json` または `text/plain`

##### 3.3.6.2 エラーメッセージ
レスポンスボディに以下のメッセージが含まれること：
- `"未找到月薪 1050000 对应的保险费等级"` または類似のエラーメッセージ

#### 3.3.7 判定基準
- HTTPステータスコードが400 Bad Requestであること
- エラーメッセージが適切に返されること
- `PremiumBracketApplicationService.socialInsuranceQuery`が1回呼び出されること

#### 3.3.8 備考
- このテストは異常系のユニットテストである
- Application層からスローされた例外がController層で適切に処理されることを確認する

---

## 4. テスト実行方法

### 4.1 テスト実行コマンド
```bash
./gradlew test
```

### 4.2 特定のテストクラスのみ実行
```bash
./gradlew test --tests SocialInsuranceControllerTest
```

### 4.3 特定のテストメソッドのみ実行
```bash
./gradlew test --tests SocialInsuranceControllerTest.testSocialInsuranceQuery_AgeOver40
```

---

## 5. テスト環境セットアップ

### 5.1 必要な環境
- Java 21以上
- Gradle

### 5.2 テストアノテーション
- `@WebFluxTest(PremiumBracketController.class)`: Controller層のみをスライスしてテスト
- `@MockitoBean`: Application層の依存関係をモック化
- `@Autowired`: WebTestClientを自動注入

---

## 6. テスト結果記録

### 6.1 テスト実行結果サマリー
| テスト項目ID | テスト項目名 | 実行日時 | 結果 | 備考 |
|------------|------------|---------|------|------|
| TC-C-001 | 社会保险金額照会（40歳以上・月給55万円） | - | - | - |
| TC-C-002 | 社会保险金額照会（40歳未満・月給28万円） | - | - | - |
| TC-C-003 | 社会保险金額照会（存在しない月給・異常系） | - | - | - |

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
- **Controller**: `src/main/java/jp/asatex/niuyuping/social_insurance_backend_service/controller/PremiumBracketController.java`
- **テストクラス**: `src/test/java/jp/asatex/niuyuping/social_insurance_backend_service/controller/SocialInsuranceControllerTest.java`

### 7.2 関連クラス
- Application層: `PremiumBracketApplicationService`
- DTO: `SocialInsuranceDto`, `SocialInsuranceApplicationDto`

---

## 8. 改訂履歴

| 版数 | 改訂日 | 改訂内容 | 改訂者 |
|-----|--------|---------|--------|
| 1.0 | - | 初版作成（Controller層ユニットテスト式様書として作成） | - |

---

**文書作成日**: -  
**最終更新日**: -  
**承認者**: -

