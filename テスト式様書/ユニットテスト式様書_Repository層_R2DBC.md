# ユニットテスト式様書（Unit Test Specification）- Repository層 - R2DBC

## 1. テスト概要

### 1.1 テスト対象システム
**システム名**: 社会保险バックエンドサービス（Social Insurance Backend Service）  
**テスト対象クラス**: `R2dbcEntityTemplate`を使用したデータアクセス  
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

### 1.3 テスト範囲
本テスト式様書は、R2DBC EntityTemplateを使用した低レベルデータアクセスのユニットテストを対象とする。  
ユニットテストでは、以下の要素をテストする：
- `R2dbcEntityTemplate`を使用したクエリ構築
- `Query`と`Criteria`を使用した動的クエリ
- `Sort`を使用したソート処理
- 実際のPostgreSQLデータベースとの連携

### 1.4 ユニットテストの特徴
- `@DataR2dbcTest`により、R2DBC関連のコンポーネントのみをロード
- `@Testcontainers`により、実際のPostgreSQLデータベースを使用
- `R2dbcEntityTemplate`を直接使用してクエリを構築
- Spring Data R2DBCのRepositoryインターフェースを使用しない

---

## 2. テスト項目一覧

| No. | テスト項目ID | テスト項目名 | テスト種別 | 優先度 |
|-----|------------|------------|----------|--------|
| 1 | TC-R2-001 | 标准报酬による保険料等級検索（正常系） | 正常系 | 高 |
| 2 | TC-R2-002 | 标准报酬による保険料等級検索（存在しない値・異常系） | 異常系 | 高 |

---

## 3. テスト項目詳細

### 3.1 TC-R2-001: 标准报酬による保険料等級検索（正常系）

#### 3.1.1 テスト目的
标准报酬（560,000円）を指定して、対応する保険料等級データが正しく取得できることを確認する。  
特に、`R2dbcEntityTemplate`を使用したクエリ構築とソート処理が正しく動作することを確認する。

#### 3.1.2 前提条件
- PostgreSQLデータベースコンテナが起動していること
- 保険料等級テーブルに必要なデータが登録されていること
- データベースマイグレーション（Flyway）が正常に実行されていること

#### 3.1.3 テストデータ
| 項目 | 値 |
|-----|-----|
| 标准报酬（stdRem） | 560,000円 |

#### 3.1.4 テスト手順
1. `R2dbcEntityTemplate`を使用してクエリを構築する
   - `Criteria.where("std_rem").is(560000)`で条件を指定
   - `Sort.by("std_rem").ascending()`でソートを指定
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
- ソートが正しく適用されること

#### 3.1.7 備考
- このテストは`R2dbcEntityTemplate`の低レベルAPIを検証する
- `Query.query()`と`Criteria`を使用した動的クエリ構築を検証する
- `Sort`を使用したソート処理を検証する

---

### 3.2 TC-R2-002: 标准报酬による保険料等級検索（存在しない値・異常系）

#### 3.2.1 テスト目的
存在しない标准报酬（1,050,000円）を指定した場合、空の結果が返されることを確認する。

#### 3.2.2 前提条件
- PostgreSQLデータベースコンテナが起動していること
- 保険料等級テーブルに必要なデータが登録されていること
- データベースマイグレーション（Flyway）が正常に実行されていること

#### 3.2.3 テストデータ
| 項目 | 値 |
|-----|-----|
| 标准报酬（stdRem） | 1,050,000円（存在しない値） |

#### 3.2.4 テスト手順
1. `R2dbcEntityTemplate`を使用してクエリを構築する
   - `Criteria.where("std_rem").is(1050000)`で条件を指定
   - `Sort.by("std_rem").ascending()`でソートを指定
2. `StepVerifier`を使用してReactive Streamを検証する
3. 空の結果が返されることを確認する

#### 3.2.5 期待結果

##### 3.2.5.1 戻り値
- `Flux<PremiumBracket>`が空であること
- データが取得されないこと

#### 3.2.6 判定基準
- Reactive Streamが正常に完了すること（`verifyComplete()`）
- `expectNextCount(0)`により、データが0件であることを確認すること
- 例外がスローされないこと

#### 3.2.7 備考
- このテストは異常系のユニットテストである
- `R2dbcEntityTemplate`では例外をスローせず、空の結果を返す
- 例外処理は上位層で行われる

---

## 4. テスト実行方法

### 4.1 テスト実行コマンド
```bash
./gradlew test
```

### 4.2 特定のテストクラスのみ実行
```bash
./gradlew test --tests SocialInsuranceR2dbcTest
```

### 4.3 特定のテストメソッドのみ実行
```bash
./gradlew test --tests SocialInsuranceR2dbcTest.testFindByStdRem
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

### 5.3 テストアノテーション
- `@DataR2dbcTest`: R2DBC関連のコンポーネントのみをロード
- `@Testcontainers`: Testcontainersの統合
- `@Container` + `@ServiceConnection`: PostgreSQLコンテナの自動起動と接続設定

---

## 6. テスト結果記録

### 6.1 テスト実行結果サマリー
| テスト項目ID | テスト項目名 | 実行日時 | 結果 | 備考 |
|------------|------------|---------|------|------|
| TC-R2-001 | 标准报酬による保険料等級検索（正常系） | - | - | - |
| TC-R2-002 | 标准报酬による保険料等級検索（存在しない値・異常系） | - | - | - |

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
- **テストクラス**: `src/test/java/jp/asatex/niuyuping/social_insurance_backend_service/repository/SocialInsuranceR2dbcTest.java`

### 7.2 関連クラス
- Entity: `PremiumBracket`
- R2DBC: `R2dbcEntityTemplate`, `Query`, `Criteria`, `Sort`

---

## 8. 改訂履歴

| 版数 | 改訂日 | 改訂内容 | 改訂者 |
|-----|--------|---------|--------|
| 1.0 | - | 初版作成（Repository層 - R2DBC ユニットテスト式様書として作成） | - |

---

**文書作成日**: -  
**最終更新日**: -  
**承認者**: -

