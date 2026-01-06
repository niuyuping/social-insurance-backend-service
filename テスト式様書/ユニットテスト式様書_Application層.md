# ユニットテスト式様書（Unit Test Specification）- Application層

## 1. テスト概要

### 1.1 テスト対象システム
**システム名**: 社会保险バックエンドサービス（Social Insurance Backend Service）  
**テスト対象クラス**: `PremiumBracketApplicationService`  
**テスト種別**: ユニットテスト（Unit Test）  
**テスト実施日**: -  
**テスト実施者**: -  
**バージョン**: 0.0.1

### 1.2 テスト環境
- **フレームワーク**: Spring Boot 4.0.0 (WebFlux)
- **テストツール**: JUnit 5, Mockito, StepVerifier, AssertJ
- **Java バージョン**: 21
- **テストアノテーション**: `@SpringBootTest(classes = PremiumBracketApplicationService.class)`

### 1.3 テスト範囲
本テスト式様書は、Application層のユニットテストを対象とする。  
ユニットテストでは、以下の要素をテストする：
- Application層のビジネスロジック
- Domain層への委譲処理
- DTO変換処理
- 例外処理（異常系）

### 1.4 ユニットテストの特徴
- `@SpringBootTest(classes = ...)`により、特定のクラスのみをロード
- `@MockitoBean`により、Domain層の依存関係をモック化
- `StepVerifier`により、Reactive Streamの検証を行う
- 実際のデータベースは使用しない

---

## 2. テスト項目一覧

| No. | テスト項目ID | テスト項目名 | テスト種別 | 優先度 |
|-----|------------|------------|----------|--------|
| 1 | TC-A-001 | 社会保险金額照会（40歳以上・月給55万円） | 正常系 | 高 |
| 2 | TC-A-002 | 社会保险金額照会（40歳未満・月給28万円） | 正常系 | 高 |
| 3 | TC-A-003 | 社会保险金額照会（存在しない月給・異常系） | 異常系 | 高 |

---

## 3. テスト項目詳細

### 3.1 TC-A-001: 社会保险金額照会（40歳以上・月給55万円）

#### 3.1.1 テスト目的
40歳以上の従業員（月給55万円）に対する社会保险金額照会処理が正しく動作することを確認する。  
特に、Domain層への委譲とDTO変換が正しく行われることを確認する。

#### 3.1.2 前提条件
- `PremiumBracketDomainService`がモック化されていること
- `PremiumBracketApplicationService`が正しく初期化されていること

#### 3.1.3 テストデータ
| 項目 | 値 |
|-----|-----|
| 月給（monthlySalary） | 550,000円 |
| 年齢（age） | 45歳 |

#### 3.1.4 モック設定
`PremiumBracketDomainService.socialInsuranceQuery(550000, 45)`が以下の値を返すように設定：
- 従業員負担額: 健康保険料27,776.00円、介護保険料4,452.00円、厚生年金51,240.00円
- 事業主負担額: 健康保険料27,776.00円、介護保険料4,452.00円、厚生年金51,240.00円

#### 3.1.5 テスト手順
1. `PremiumBracketDomainService`のモックを設定する
2. `PremiumBracketApplicationService.socialInsuranceQuery(550000, 45)`を呼び出す
3. `StepVerifier`を使用してReactive Streamを検証する
4. レスポンスの各項目を検証する

#### 3.1.6 期待結果

##### 3.1.6.1 戻り値（従業員負担額）
| 項目 | 期待値 | 単位 |
|-----|--------|------|
| 健康保険料（介護なし） | 27,776.00 | 円 |
| 介護保険料 | 4,452.00 | 円 |
| 厚生年金保険料 | 51,240.00 | 円 |

##### 3.1.6.2 戻り値（事業主負担額）
| 項目 | 期待値 | 単位 |
|-----|--------|------|
| 健康保険料（介護なし） | 27,776.00 | 円 |
| 介護保険料 | 4,452.00 | 円 |
| 厚生年金保険料 | 51,240.00 | 円 |

#### 3.1.7 判定基準
- Reactive Streamが正常に完了すること（`verifyComplete()`）
- レスポンスの各項目が期待値と一致すること
- `PremiumBracketDomainService.socialInsuranceQuery`が1回呼び出されること
- DTO変換が正しく行われること

#### 3.1.8 備考
- Domain層のロジックはテスト対象外（モック化されている）
- このテストはApplication層の委譲処理とDTO変換のみを検証する
- `StepVerifier`を使用することで、Reactive Streamの非同期処理を適切に検証する

---

### 3.2 TC-A-002: 社会保险金額照会（40歳未満・月給28万円）

#### 3.2.1 テスト目的
40歳未満の従業員（月給28万円）に対する社会保险金額照会処理が正しく動作することを確認する。  
特に、介護保険が適用されない場合の処理を確認する。

#### 3.2.2 前提条件
- `PremiumBracketDomainService`がモック化されていること
- `PremiumBracketApplicationService`が正しく初期化されていること

#### 3.2.3 テストデータ
| 項目 | 値 |
|-----|-----|
| 月給（monthlySalary） | 280,000円 |
| 年齢（age） | 25歳 |

#### 3.2.4 モック設定
`PremiumBracketDomainService.socialInsuranceQuery(280000, 25)`が以下の値を返すように設定：
- 従業員負担額: 健康保険料13,888.00円、介護保険料0.00円、厚生年金25,620.00円
- 事業主負担額: 健康保険料13,888.00円、介護保険料0.00円、厚生年金25,620.00円

#### 3.2.5 テスト手順
1. `PremiumBracketDomainService`のモックを設定する
2. `PremiumBracketApplicationService.socialInsuranceQuery(280000, 25)`を呼び出す
3. `StepVerifier`を使用してReactive Streamを検証する
4. レスポンスの各項目を検証する

#### 3.2.6 期待結果

##### 3.2.6.1 戻り値（従業員負担額）
| 項目 | 期待値 | 単位 |
|-----|--------|------|
| 健康保険料（介護なし） | 13,888.00 | 円 |
| 介護保険料 | 0.00 | 円 |
| 厚生年金保険料 | 25,620.00 | 円 |

##### 3.2.6.2 戻り値（事業主負担額）
| 項目 | 期待値 | 単位 |
|-----|--------|------|
| 健康保険料（介護なし） | 13,888.00 | 円 |
| 介護保険料 | 0.00 | 円 |
| 厚生年金保険料 | 25,620.00 | 円 |

#### 3.2.7 判定基準
- Reactive Streamが正常に完了すること（`verifyComplete()`）
- レスポンスの各項目が期待値と一致すること
- 介護保険料が0.00円であること（40歳未満であるため）
- `PremiumBracketDomainService.socialInsuranceQuery`が1回呼び出されること

#### 3.2.8 備考
- 介護保険料が0.00円であることを確認することで、DTO変換が正しく行われていることを検証する

---

### 3.3 TC-A-003: 社会保险金額照会（存在しない月給・異常系）

#### 3.3.1 テスト目的
存在しない月給を指定した場合、適切な例外がスローされることを確認する。

#### 3.3.2 前提条件
- `PremiumBracketDomainService`がモック化されていること
- `PremiumBracketApplicationService`が正しく初期化されていること

#### 3.3.3 テストデータ
| 項目 | 値 |
|-----|-----|
| 月給（monthlySalary） | 1,050,000円（存在しない値） |
| 年齢（age） | 30歳 |

#### 3.3.4 モック設定
`PremiumBracketDomainService.socialInsuranceQuery(1050000, 30)`が`IllegalArgumentException`をスローするように設定：
- エラーメッセージ: `"未找到月薪 1050000 对应的保险费等级"`

#### 3.3.5 テスト手順
1. `PremiumBracketDomainService`のモックを設定する（エラーを返すように）
2. `PremiumBracketApplicationService.socialInsuranceQuery(1050000, 30)`を呼び出す
3. `StepVerifier`を使用して例外を検証する

#### 3.3.6 期待結果

##### 3.3.6.1 例外
- **例外タイプ**: `IllegalArgumentException`
- **エラーメッセージ**: `"未找到月薪 1050000 对应的保险费等级"`を含む

#### 3.3.7 判定基準
- `IllegalArgumentException`がスローされること
- エラーメッセージが期待値と一致すること
- `PremiumBracketDomainService.socialInsuranceQuery`が1回呼び出されること
- Reactive Streamがエラーで終了すること（`expectErrorMatches`）

#### 3.3.8 備考
- このテストは異常系のユニットテストである
- Domain層からスローされた例外がApplication層で適切に伝播されることを確認する
- `StepVerifier.expectErrorMatches`を使用することで、例外の型とメッセージを検証する

---

## 4. テスト実行方法

### 4.1 テスト実行コマンド
```bash
./gradlew test
```

### 4.2 特定のテストクラスのみ実行
```bash
./gradlew test --tests SocialInsuranceApplicationServiceTest
```

### 4.3 特定のテストメソッドのみ実行
```bash
./gradlew test --tests SocialInsuranceApplicationServiceTest.testSocialInsuranceApplicationService_AgeOver40
```

---

## 5. テスト環境セットアップ

### 5.1 必要な環境
- Java 21以上
- Gradle

### 5.2 テストアノテーション
- `@SpringBootTest(classes = PremiumBracketApplicationService.class)`: 特定のクラスのみをロード
- `@MockitoBean`: Domain層の依存関係をモック化
- `@Autowired`: Application層のサービスを自動注入

---

## 6. テスト結果記録

### 6.1 テスト実行結果サマリー
| テスト項目ID | テスト項目名 | 実行日時 | 結果 | 備考 |
|------------|------------|---------|------|------|
| TC-A-001 | 社会保险金額照会（40歳以上・月給55万円） | - | - | - |
| TC-A-002 | 社会保险金額照会（40歳未満・月給28万円） | - | - | - |
| TC-A-003 | 社会保险金額照会（存在しない月給・異常系） | - | - | - |

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
- **Application層**: `src/main/java/jp/asatex/niuyuping/social_insurance_backend_service/application/PremiumBracketApplicationService.java`
- **テストクラス**: `src/test/java/jp/asatex/niuyuping/social_insurance_backend_service/application/SocialInsuranceApplicationServiceTest.java`

### 7.2 関連クラス
- Domain層: `PremiumBracketDomainService`
- DTO: `SocialInsuranceApplicationDto`, `SocialInsuranceDomainDto`

---

## 8. 改訂履歴

| 版数 | 改訂日 | 改訂内容 | 改訂者 |
|-----|--------|---------|--------|
| 1.0 | - | 初版作成（Application層ユニットテスト式様書として作成） | - |

---

**文書作成日**: -  
**最終更新日**: -  
**承認者**: -

