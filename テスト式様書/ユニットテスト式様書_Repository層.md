# ユニットテスト式様書（Unit Test Specification）- Repository層

## 1. テスト概要

### 1.1 テスト対象システム
**システム名**: 社会保险バックエンドサービス（Social Insurance Backend Service）  
**テスト対象クラス**: `PremiumBracketRepository`  
**テスト種別**: ユニットテスト（Unit Test）  
**テスト実施日**: -  
**テスト実施者**: -  
**バージョン**: 0.0.1

### 1.2 テスト環境
- **フレームワーク**: Spring Boot 4.0.0 (WebFlux)
- **データベース**: PostgreSQL 17.4 (Testcontainers)
- **テストツール**: JUnit 5, StepVerifier, AssertJ
- **Java バージョン**: 21
- **テストアノテーション**: `@DataR2dbcTest`, `@Testcontainers`
- **テストプロファイル**: `test`

### 1.3 テスト範囲
本テスト式様書は、Repository層（Spring Data R2DBC）のユニットテストを対象とする。  
ユニットテストでは、以下の要素をテストする：
- Spring Data R2DBC Repositoryのクエリメソッド
- 実際のPostgreSQLデータベースとの連携
- データ取得処理（正常系・異常系）

### 1.4 ユニットテストの特徴
- `@DataR2dbcTest`により、R2DBC関連のコンポーネントのみをロード
- `@Testcontainers`により、実際のPostgreSQLデータベースを使用
- `@ActiveProfiles("test")`により、テスト用の設定を適用
- データベースマイグレーション（Flyway）が自動実行される

---

## 2. テスト項目一覧

| No. | テスト項目ID | テスト項目名 | テスト種別 | 優先度 |
|-----|------------|------------|----------|--------|
| 1 | TC-R-001 | 月給による保険料等級検索（正常系） | 正常系 | 高 |
| 2 | TC-R-002 | 月給による保険料等級検索（存在しない値・異常系） | 異常系 | 高 |

---

## 3. テスト項目詳細

### 3.1 TC-R-001: 月給による保険料等級検索（正常系）

#### 3.1.1 テスト目的
月給（550,000円）を指定して、対応する保険料等級データが正しく取得できることを確認する。

#### 3.1.2 前提条件
- PostgreSQLデータベースコンテナが起動していること
- 保険料等級テーブルに必要なデータが登録されていること
- データベースマイグレーション（Flyway）が正常に実行されていること

#### 3.1.3 テストデータ
| 項目 | 値 |
|-----|-----|
| 月給（amount） | 550,000円 |

#### 3.1.4 テスト手順
1. `PremiumBracketRepository.findByAmount(550000)`を呼び出す
2. `StepVerifier`を使用してReactive Streamを検証する
3. 取得したデータの各項目を検証する

#### 3.1.5 期待結果

##### 3.1.5.1 取得データ
| 項目 | 期待値 |
|-----|--------|
| 等級（grade） | `"32(29)"` |
| 标准报酬（stdRem） | 560,000円 |
| 最小値（minAmount） | 545,000円 |
| 最大値（maxAmount） | 575,000円 |
| 健康保険料（介護なし） | 55,552.00円 |
| 健康保険料（介護あり） | 64,456.00円 |
| 厚生年金保険料 | 102,480.00円 |

#### 3.1.6 判定基準
- Reactive Streamが正常に完了すること（`verifyComplete()`）
- 取得したデータが期待値と一致すること
- `id`、`createdAt`、`updatedAt`フィールドは比較対象外（`ignoringFields`を使用）
- `usingRecursiveComparison`を使用して、オブジェクト全体を比較すること

#### 3.1.7 備考
- このテストは実際のデータベースを使用するため、統合テストに近い性質を持つ
- `@DataR2dbcTest`により、R2DBC関連のコンポーネントのみがロードされる
- データベースのクエリロジック（`@Query`アノテーション）を検証する

---

### 3.2 TC-R-002: 月給による保険料等級検索（存在しない値・異常系）

#### 3.2.1 テスト目的
存在しない月給（1,050,000円）を指定した場合、空の結果が返されることを確認する。

#### 3.2.2 前提条件
- PostgreSQLデータベースコンテナが起動していること
- 保険料等級テーブルに必要なデータが登録されていること
- データベースマイグレーション（Flyway）が正常に実行されていること

#### 3.2.3 テストデータ
| 項目 | 値 |
|-----|-----|
| 月給（amount） | 1,050,000円（存在しない値） |

#### 3.2.4 テスト手順
1. `PremiumBracketRepository.findByAmount(1050000)`を呼び出す
2. `StepVerifier`を使用してReactive Streamを検証する
3. 空の結果が返されることを確認する

#### 3.2.5 期待結果

##### 3.2.5.1 戻り値
- `Mono<PremiumBracket>`が空（`Mono.empty()`）であること
- データが取得されないこと

#### 3.2.6 判定基準
- Reactive Streamが正常に完了すること（`verifyComplete()`）
- `expectNextCount(0)`により、データが0件であることを確認すること
- 例外がスローされないこと

#### 3.2.7 備考
- このテストは異常系のユニットテストである
- Repository層では例外をスローせず、空の結果を返す
- 例外処理はDomain層で行われる

---

## 4. テスト実行方法

### 4.1 テスト実行コマンド
```bash
./gradlew test
```

### 4.2 特定のテストクラスのみ実行
```bash
./gradlew test --tests SocialInsuranceRepositoryTest
```

### 4.3 特定のテストメソッドのみ実行
```bash
./gradlew test --tests SocialInsuranceRepositoryTest.testFindBracketByAmount
```

---

## 5. テスト環境セットアップ

### 5.1 必要な環境
- Java 21以上
- Docker（Testcontainers用）
- Gradle

### 5.2 テストデータベース
- Testcontainersにより、自動的にPostgreSQL 17.4コンテナが起動される
- データベースマイグレーション（Flyway）が自動実行される
- テストプロファイル（`test`）の設定が適用される

### 5.3 テストアノテーション
- `@DataR2dbcTest`: R2DBC関連のコンポーネントのみをロード
- `@Testcontainers`: Testcontainersの統合
- `@Container` + `@ServiceConnection`: PostgreSQLコンテナの自動起動と接続設定
- `@ActiveProfiles("test")`: テストプロファイルの有効化

---

## 6. テスト結果記録

### 6.1 テスト実行結果サマリー
| テスト項目ID | テスト項目名 | 実行日時 | 結果 | 備考 |
|------------|------------|---------|------|------|
| TC-R-001 | 月給による保険料等級検索（正常系） | - | - | - |
| TC-R-002 | 月給による保険料等級検索（存在しない値・異常系） | - | - | - |

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
- **Repository**: `src/main/java/jp/asatex/niuyuping/social_insurance_backend_service/repository/PremiumBracketRepository.java`
- **テストクラス**: `src/test/java/jp/asatex/niuyuping/social_insurance_backend_service/repository/SocialInsuranceRepositoryTest.java`

### 7.2 関連クラス
- Entity: `PremiumBracket`
- Repository実装: `PremiumBracketRepositoryImpl`

---

## 8. 改訂履歴

| 版数 | 改訂日 | 改訂内容 | 改訂者 |
|-----|--------|---------|--------|
| 1.0 | - | 初版作成（Repository層ユニットテスト式様書として作成） | - |

---

**文書作成日**: -  
**最終更新日**: -  
**承認者**: -

