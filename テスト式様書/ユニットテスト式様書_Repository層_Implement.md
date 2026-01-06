# ユニットテスト式様書（Unit Test Specification）- Repository層 - Implement

## 1. テスト概要

### 1.1 テスト対象システム
**システム名**: 社会保险バックエンドサービス（Social Insurance Backend Service）  
**テスト対象クラス**: `PremiumBracketRepositoryImpl`  
**テスト種別**: ユニットテスト（Unit Test）  
**テスト実施日**: -  
**テスト実施者**: -  
**バージョン**: 0.0.1

### 1.2 テスト環境
- **フレームワーク**: Spring Boot 4.0.0 (WebFlux)
- **テストツール**: JUnit 5, Mockito, StepVerifier, AssertJ
- **Java バージョン**: 21
- **テストアノテーション**: `@SpringBootTest(classes = PremiumBracketRepositoryImpl.class)`

### 1.3 テスト範囲
本テスト式様書は、RepositoryImpl層のユニットテストを対象とする。  
ユニットテストでは、以下の要素をテストする：
- `PremiumBracketRepositoryImpl`のカスタム実装メソッド
- `R2dbcEntityTemplate`を使用した複雑なクエリ処理
- モックを使用した依存関係の分離

### 1.4 ユニットテストの特徴
- `@SpringBootTest(classes = ...)`により、特定のクラスのみをロード
- `@MockitoBean`により、`R2dbcEntityTemplate`をモック化
- 実際のデータベースは使用しない
- 複雑なクエリチェーンのモック化を検証する

---

## 2. テスト項目一覧

| No. | テスト項目ID | テスト項目名 | テスト種別 | 優先度 |
|-----|------------|------------|----------|--------|
| 1 | TC-RI-001 | 月給による保険料等級検索（正常系） | 正常系 | 高 |
| 2 | TC-RI-002 | 月給による保険料等級検索（存在しない値・異常系） | 異常系 | 高 |

---

## 3. テスト項目詳細

### 3.1 TC-RI-001: 月給による保険料等級検索（正常系）

#### 3.1.1 テスト目的
`PremiumBracketRepositoryImpl.findBracketByAmount`メソッドが正しく動作することを確認する。  
特に、`R2dbcEntityTemplate`を使用した複雑なクエリチェーンが正しく構築されることを確認する。

#### 3.1.2 前提条件
- `R2dbcEntityTemplate`がモック化されていること
- `PremiumBracketRepositoryImpl`が正しく初期化されていること

#### 3.1.3 テストデータ
| 項目 | 値 |
|-----|-----|
| 月給（amount） | 550,000円 |

#### 3.1.4 モック設定
`R2dbcEntityTemplate`のクエリチェーンをモック化：
- `select(PremiumBracket.class)` → `ReactiveSelect`
- `matching(Query)` → `TerminatingSelect`
- `first()` → `Mono<PremiumBracket>`（正常なデータを返す）

#### 3.1.5 テスト手順
1. `R2dbcEntityTemplate`のモックを設定する（クエリチェーン全体をモック化）
2. `PremiumBracketRepositoryImpl.findBracketByAmount(550000)`を呼び出す
3. `StepVerifier`を使用してReactive Streamを検証する
4. 取得したデータの各項目を検証する

#### 3.1.6 期待結果

##### 3.1.6.1 取得データ
| 項目 | 期待値 |
|-----|--------|
| 等級（grade） | `"32(29)"` |
| 标准报酬（stdRem） | 560,000円 |
| 最小値（minAmount） | 545,000円 |
| 最大値（maxAmount） | 575,000円 |
| 健康保険料（介護なし） | 55,552.00円 |
| 健康保険料（介護あり） | 64,456.00円 |
| 厚生年金保険料 | 102,480.00円 |

#### 3.1.7 判定基準
- Reactive Streamが正常に完了すること（`verifyComplete()`）
- 取得したデータが期待値と一致すること
- `id`、`createdAt`、`updatedAt`フィールドは比較対象外（`ignoringFields`を使用）
- `usingRecursiveComparison`を使用して、オブジェクト全体を比較すること
- `R2dbcEntityTemplate`のクエリチェーンが正しく呼び出されること

#### 3.1.8 備考
- このテストは`PremiumBracketRepositoryImpl`のカスタム実装を検証する
- `R2dbcEntityTemplate`の複雑なクエリチェーンをモック化する必要がある
- 実際のデータベースは使用せず、モックを使用してロジックのみを検証する

---

### 3.2 TC-RI-002: 月給による保険料等級検索（存在しない値・異常系）

#### 3.2.1 テスト目的
存在しない月給（1,050,000円）を指定した場合、空の結果が返されることを確認する。

#### 3.2.2 前提条件
- `R2dbcEntityTemplate`がモック化されていること
- `PremiumBracketRepositoryImpl`が正しく初期化されていること

#### 3.2.3 テストデータ
| 項目 | 値 |
|-----|-----|
| 月給（amount） | 1,050,000円（存在しない値） |

#### 3.2.4 モック設定
`R2dbcEntityTemplate`のクエリチェーンをモック化：
- `select(PremiumBracket.class)` → `ReactiveSelect`
- `matching(Query)` → `TerminatingSelect`
- `first()` → `Mono.empty()`（空の結果を返す）

#### 3.2.5 テスト手順
1. `R2dbcEntityTemplate`のモックを設定する（空の結果を返すように）
2. `PremiumBracketRepositoryImpl.findBracketByAmount(1050000)`を呼び出す
3. `StepVerifier`を使用してReactive Streamを検証する
4. 空の結果が返されることを確認する

#### 3.2.6 期待結果

##### 3.2.6.1 戻り値
- `Mono<PremiumBracket>`が空（`Mono.empty()`）であること
- データが取得されないこと

#### 3.2.7 判定基準
- Reactive Streamが正常に完了すること（`verifyComplete()`）
- `expectNextCount(0)`により、データが0件であることを確認すること
- 例外がスローされないこと
- `R2dbcEntityTemplate`のクエリチェーンが正しく呼び出されること

#### 3.2.8 備考
- このテストは異常系のユニットテストである
- RepositoryImpl層では例外をスローせず、空の結果を返す
- 例外処理は上位層で行われる

---

## 4. テスト実行方法

### 4.1 テスト実行コマンド
```bash
./gradlew test
```

### 4.2 特定のテストクラスのみ実行
```bash
./gradlew test --tests SocialInsuranceRepositoryImplTest
```

### 4.3 特定のテストメソッドのみ実行
```bash
./gradlew test --tests SocialInsuranceRepositoryImplTest.testFindBracketByAmount
```

---

## 5. テスト環境セットアップ

### 5.1 必要な環境
- Java 21以上
- Gradle

### 5.2 テストアノテーション
- `@SpringBootTest(classes = PremiumBracketRepositoryImpl.class)`: 特定のクラスのみをロード
- `@MockitoBean`: `R2dbcEntityTemplate`をモック化
- `@Autowired`: RepositoryImplを自動注入

---

## 6. テスト結果記録

### 6.1 テスト実行結果サマリー
| テスト項目ID | テスト項目名 | 実行日時 | 結果 | 備考 |
|------------|------------|---------|------|------|
| TC-RI-001 | 月給による保険料等級検索（正常系） | - | - | - |
| TC-RI-002 | 月給による保険料等級検索（存在しない値・異常系） | - | - | - |

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
- **RepositoryImpl**: `src/main/java/jp/asatex/niuyuping/social_insurance_backend_service/repository/impl/PremiumBracketRepositoryImpl.java`
- **テストクラス**: `src/test/java/jp/asatex/niuyuping/social_insurance_backend_service/repository/SocialInsuranceRepositoryImplTest.java`

### 7.2 関連クラス
- Entity: `PremiumBracket`
- R2DBC: `R2dbcEntityTemplate`, `Query`, `Criteria`

---

## 8. 改訂履歴

| 版数 | 改訂日 | 改訂内容 | 改訂者 |
|-----|--------|---------|--------|
| 1.0 | - | 初版作成（Repository層 - Implementユニットテスト式様書として作成） | - |

---

**文書作成日**: -  
**最終更新日**: -  
**承認者**: -

