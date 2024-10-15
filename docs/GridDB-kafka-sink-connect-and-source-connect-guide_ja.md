# GridDBとApache Kafkaとの連携方法

## はじめに

今日のアプリケーションやIoTデバイスは、多くのビッグデータを生成しています。このデータの大半は非構造化データです。非常に長い間、構造化されたデータを保存するために、リレーショナルデータベース管理システムが使用されてきました。これらのデータベース管理システムは、データを行と列で構成されるテーブルに整理しています。しかし、非構造化データは行と列でうまく整理できないため、リレーショナルデータベースは非構造化データの保存に最適な選択肢ではありません。NoSQLデータベースは、リレーショナルデータベースで生じたギャップを埋めるために開発されたので非構造化データの保存に使いやすくなっています。GridDBは、NoSQLデータベースの良い例であり、IoTやビッグデータ向けに最適化されています。GridDBを使用する場合、データのストリームインとアウトが必要になります。Apache Kafkaは、これを実現するためのデータストリーミングプラットフォームです。今回は、KafkaとGridDBデータベースを使用する際に、ソフトウェアシステムのさまざまな要素間の通信をどのように整理するかについて説明します。

### Apache Kafka

Apache Kafka は、データ統合、データパイプライン、ストリーミング分析に使用される分散イベント ストリーミング プラットフォームです。ストリーミングデータをリアルタイムで取り込み、処理するのに役立ちます。ストリーミングデータとは、データソースによって継続的に生成され、データレコードを同時に送信するデータのことです。Apache Kafka は、ユーザーに次の機能を提供します。

* ストリーミングデータのパブリッシュ、サブスクライブ
* ストリーミングデータを生成された順序で効率的に保存
* ストリーミングデータのリアルタイム処理

Apache Kafka は、ストレージ、メッセージング、ストリーム処理を組み合わせて、リアルタイムデータとヒストリカルデータの分析を容易にします。

### GridDB

* GridDBはNoSQLインターフェースとSQLインタフェースを備えたIoT向けデータベースです。

### GridDB Kafka Sinkコネクタ
* Apacke KafkaのトピックからデータをGridDBのコンテナ(テーブル)に登録します。

### GridDB Kafka Sourceコネクタ
* GridDBのコンテナ(テーブル)からデータをApacke Kafkaのトピックに登録します。

### Apache Kafkaデータ型とGridDBデータ型の対応関係

* 以下のようにSourceコネクタで自動的にデータ型がマッピングされます。

    | Apache Kafkaデータ型  | GridDBデータ型 |
    |-------------------------|------------------|
    | INT8                    | GSType.BYTE      |
    | INT16                   | GSType.SHORT     |
    | INT32                   | GSType.INTEGER   |
    | INT64                   | GSType.LONG      |
    | FLOAT32                 | GSType.FLOAT     |
    | FLOAT64                 | GSType.DOUBLE    |
    | BOOLEAN                 | GSType.BOOL      |
    | STRING                  | GSType.STRING    |
    | BYTES                   | GSType.BLOB      |
    | Timestamp               | GSType.TIMESTAMP |

* Sinkコネクタでのデータ型対応関係:

    | Apache Kafkaデータ型  | GridDBデータ型 |
    |-------------------------|------------------|
    | INT8                    | GSType.BYTE      |
    | INT16                   | GSType.SHORT     |
    | INT32                   | GSType.INTEGER   |
    | INT64                   | GSType.LONG      |
    | FLOAT32                 | GSType.FLOAT     |
    | FLOAT64                 | GSType.DOUBLE    |
    | BOOLEAN                 | GSType.BOOL      |
    | STRING                  | GSType.STRING    |
    | BYTES                   | GSType.BLOB      |
    | Decimal, Date, Time, Timestamp | GSType.TIMESTAMP |

※ GridDBでは、ロウキーは先頭カラムがロウキーとなります。

### Apache KafkaとGridDBの用語の対応関係について

* 用語

    | Apache Kafka | GridDB |
    |-------------------------|-------------------|
    | トピック(topic)                   | コンテナ(container)、テーブル(table)  |
    | レコード(record)                  | ロウ(row)               |
    | フィールド(field)                   | カラム(column)            |

    ※ 例は[ここ](#relations)を見てください。 

## クイックスタート

### 動作環境

以下の環境でビルドとサンプルプログラムの実行を確認しています。

    OS: Ubuntu 24.04(x64)
    Java: 17
    Maven: 3.9.5
    Kafka: 2.13-3.7.1
    GridDB server: V5.6 CE, Ubuntu 22.04(x64)

### Apache Kafkaのインストールと開始

```console
$ wget https://dlcdn.apache.org/kafka/3.7.1/kafka_2.13-3.7.1.tgz
$ tar xzvf kafka_2.13-3.7.1.tgz
$ cd kafka_2.13-3.7.1
$ export PATH=$PATH:/path/to/kafka_2.13-3.7.1/bin
$ zookeeper-server-start.sh -daemon config/zookeeper.properties # zookeeper serverの開始
$ kafka-server-start.sh config/server.properties # Apache Kafka serverの開始
```

※
* Apache Kafka serverの実行を継続させるために起動した端末はそのままにしてください。
* もし起動に失敗する場合は以下の対応をしてみてください。
    * server.propertiesファイルを開いて、"#listeners=PLAINTEXT://:9092"の行のコメントを外して、
    "listeners=PLAINTEXT://127.0.0.1:9092"に変更してみてください。

### GridDBサーバのインストール
1. [パッケージもしくはソースを利用する場合](https://github.com/griddb/griddb)
2. [Dockerイメージを利用する場合](https://github.com/griddb/griddb-docker/tree/main/griddb)
    * GridDB サーバーを起動するための簡単な docker コマンド
        ```console
        $ docker pull griddb/griddb
        $ docker run --network="host" -e GRIDDB_CLUSTER_NAME=griddb griddb/griddb
        ```

※ Dockerイメージを利用する場合、GridDBサーバをインストール・起動した端末は開いたままにしてください。

### GridDB Kafka Sinkコネクタ/GridDB Kafka Sourceコネクタのビルド

1. GridDB Kafka Sinkコネクタ/GridDB Kafka Sourceコネクタのビルド

    ```console
    $ cd GRIDDB_KAFKA_CONNECTOR_FOLDER
    $ mvn clean install
    ```

    griddb-kafka-connector-X.X.jarファイルが"target/"フォルダに作成されます。

2. 作成されたgriddb-kafka-connector-X.X.jarファイルを/path/to/kafka_2.13-3.7.1/libs/にコピーしてください。

## GridDB Kafka Sinkコネクタについて

### Apache Kafkaのトピックとイベントの生成

1. データの準備

* samples/sinkフォルダ内の"rawdata.txt"がサンプルデータです。

2. Apache Kafkaを動かすためのスクリプトの準備

* samples/sinkフォルダ内の"script_sink.sh"がサンプルスクリプトです。

3. スクリプトの実行

* 環境変数PATHの設定
    ```console
    $ export PATH=$PATH:/path/to/kafka_2.13-3.7.1/bin
    ```
* script_sink.shの実行

    ```console
    $ ./script_sink.sh
    ```
    => output:
    ```console
    ./rawdata.txt
    Creating topic topic_D001
    >>Creating topic topic_D002
    ...
    ```

### GridDB Kafka Sinkコネクタの実行

1. GRIDDB_KAFKA_CONNECTOR_FOLDER/config/griddb-sink.propertiesファイルの編集

* topics.regexプロパティの利用例:
    ```properties
    topics.regex=topic.(.*)
    ```
* topicプロパティの利用例:
    ```properties
    topics=topic_D001,topic_D002
    ```
2. 新しい端末を開いて、以下を実行
    ```console
    $ cd kafka_2.13-3.7.1
    $ ./bin/connect-standalone.sh config/connect-standalone.properties GRIDDB_KAFKA_CONNECTOR_FOLDER/config/griddb-sink.properties
    ```
    GridDBにデータが登録されます。

### 登録されたデータの確認(GridDB CLIによる)

```console
$ gs_sh
$ gs[public]> sql select * from topic_D001;
1 results. (2 ms)
$ gs[public]> get
datetime,sensor,translate01,translate02,message,sensoractivity
2012-07-18T00:54:45.000Z,D001,Ignore,Ignore,CLOSE,Control4-Door
The 1 results had been acquired.
```

### GridDB Kafka Sinkコネクタのコンフィグ

* パラメータ一覧

    |パラメータ名|説明|デフォルト値|
    |---|---|---|
    |connector.class|the sink connector class|com.github.griddb.kafka.connect.GriddbSinkConnector|
    |name|コネクタ名|   |
    |topics.regex|topic名(正規表現)|   |
    |topics|topic名(列挙)|   |
    |host|GridDBサーバ接続用のホスト名もしくはマルチキャストアドレス|   |
    |port|GridDBサーバ接続用のポートNoもしくはマルチキャストポートNo|   |
    |cluster.name|GridDBクラスタ名|   |
    |user|GridDBユーザ名|   |
    |password|GridDBパスワード|   |
    |notification.member|固定リスト方式のGridDB notification member list|   |
    |batch.size|GridDBサーバへのバッチ登録件数|3000|
    |multiput|GridDB JavaAPIのmultiputメソッドの利用の有無|true|
    |container.name.format|using it to change to topic name from GridDB container|$(topic): The default container name is topic name |
    |container.type|TIME_SERIESを指定かつ先頭カラムがTIMESTAMP型の場合は、時系列コンテナを生成。それ以外はコレクションコンテナを生成。|COLLECTION |
    
    ※
    * connector.class, name, topics.regex or topics, transformsはApache Kafkaで使われているプロパティです。
    * "topics.regex"、"topics"のいずれか1つを設定してください。

## GridDB Kafka Sourceコネクタについて

### GridDB側のデータ準備

* [GridDB Python API](https://github.com/griddb/python_client)のインストール

* サンプル"topic.py"の実行
    * samples/sourceの「topic.py」は、GridDB Python APIを使用して GridDBにデータをプッシュするためのサンプルファイルです。

    ```console
    $ python3 topic.py 239.0.0.1 31999 griddb admin admin
    ```

* <a id="relations"></a>Apache KafkaとGridDBの用語の対応関係:

    | Apache Kafka | GridDB  | 値の例  |
    |--------------|------------------|--------|
    | トピック(topic)        | コンテナ(container)、テーブル(table) |"D001", "D002", "T102",....|
    | レコード(record)       | ロウ(row)              | ['2020-10-01T15:00:00.000Z', False, 1, "griddb"],... |
    | フィールド(field)        | カラム(column)           |"name","status","count","sign"|

### GridDB Kafka Sourceコネクタの実行

1. GRIDDB_KAFKA_CONNECTOR_FOLDER/config/griddb-source.propertiesファイルの編集
    * Timestampモードの設定例
        ```properties
        containers=D001,D002,T102,BATP102,T103,BATP103,T101,MA016,D003,M009
        mode=timestamp
        timestamp.column.name=name
        ```

    * Bulkモードの設定例
        ```properties
        containers=D001,D002,T102,BATP102,T103,BATP103,T101,MA016,D003,M009
        mode=bulk
        ```
    ※
    * Timestampモード:GridDBからデータを取得するために、1カラムはtimestamp型でなければなりません。"timestamp.column.name"プロパティでそのカラム名を指定してください。
    * Bulkモード:"timestamp.column.name"プロパティの設定は不要です。繰り返しデータ取得を繰り返します。

2. 新しい端末を開いて、以下を実行
    ```console
    $ cd kafka_2.13-3.7.1
    $ ./bin/connect-standalone.sh config/connect-standalone.properties GRIDDB_KAFKA_CONNECTOR_FOLDER/config/griddb-source.properties
    ```
※
* GridDB Kafka SinkコネクタとGridDB Kafka Sourceコネクタはデフォルトで同じポート (9092) で実行されるため、SinkコネクタとSourceコネクタの両方を実行する場合は、実行する前にApache Kafkaのポートを設定してください。
* さらに、Apache Kafka の他のメソッドを実行する前に、現在のプロセスを停止します。

### トピックをチェックするためのApache Kafkaのコマンドの利用方法

1. 利用可能なトピック一覧の取得:
    ```console
    $ ./bin/kafka-topics.sh --list --bootstrap-server localhost:9092
    BATP102
    BATP103
    ...
    ```
2. トピックについてデータのチェック・確認:

* Timestampモード
    ```console
    $ ./bin/kafka-console-consumer.sh --topic BATP102 --from-beginning --bootstrap-server localhost:9092
    {"schema":{"type":"struct","fields":[{"type":"int64","optional":false,"name":"org.apache.kafka.connect.data.Timestamp","version":1,"field":"name"},{"type":"boolean","optional":true,"field":"status"},{"type":"int64","optional":true,"field":"count"},{"type":"string","optional":true,"field":"sign"}],"optional":false,"name":"D001"},"payload":{"name":1601564400000,"status":false,"count":1,"sign":"griddb"}}
    ```

* Bulkモード
    ```console
    $ ./bin/kafka-console-consumer.sh --topic BATP102 --from-beginning --bootstrap-server localhost:9092
    {"schema":{"type":"struct","fields":[{"type":"int64","optional":false,"name":"org.apache.kafka.connect.data.Timestamp","version":1,"field":"name"},{"type":"boolean","optional":true,"field":"status"},{"type":"int64","optional":true,"field":"count"},{"type":"string","optional":true,"field":"sign"}],"optional":false,"name":"D002"},"payload":{"name":1601564400000,"status":false,"count":1,"sign":"griddb"}}
    ```

### GridDB Kafka Sourceコネクタのコンフィグ

* パラメータ一覧

    | パラメータ名             | 説明                                                  | デフォルト値                                         |
    | --------------------- | ------------------------------------------------------------ | ----------------------------------------------------- |
    | connector.class       | Sourceコネクタのクラス                                   | com.github.griddb.kafka.connect.GriddbSourceConnector |
    | name                  | コネクタ名                                          |                                                       |
    | host                  | GridDBサーバ接続用のホスト名もしくはマルチキャストアドレス|                                                       |
    | port                  | GridDBサーバ接続用のポートNoもしくはマルチキャストポートNo|                                                       |
    | cluster.name          | GridDBクラスタ名|                                                       |
    | user                  | GridDBユーザ名|                                                       |
    | password              | GridDBパスワード|                                                       |
    | notification.member   | 固定リスト方式のGridDB notification member list|                                                       |
    | containers            | コンテナ名一覧|                                                       |
    | mode                  | BulkモードもしくはTimestampモード                          |                                                       |
    | timestamp.column.name | TIMESTAMP型カラムの一覧(Timestampモード利用時)|                                                       |
    | batch.max.rows        | バッチ処理の最大ロウ数| 100                                                   |
    | topic.prefix          | 出力トピックのプレフィックス|                                                       |
    | polling.interval.ms   | データ取得(poll)の間隔(ms)| 5000                                                  |

    ※ connector.class, nameはApache Kafkaで使われているプロパティです。

