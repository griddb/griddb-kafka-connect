GridDB Kafkaコネクタ

# 概要

GridDB Kafkaコネクタは、[Apache Kafka](https://kafka.apache.org/)とGridDBデータベースとの間でデータのやり取りを可能にする
ソフトウェアです。

# 動作環境

以下の環境でビルドとサンプルプログラムの実行を確認しています。

    OS: Ubuntu 24.04(x64)
    Java: 17
    Maven: 3.9.5
    Kafka: 2.13-3.7.1
    GridDB server: V5.6 CE, Ubuntu 22.04(x64)

# ビルド

```console
$ cd GRIDDB_KAFKA_CONNECTOR_FOLDER
$ mvn clean install
```

# 実行

ビルド後、GRIDDB_KAFKA_CONNECTOR_FOLDER/target/griddb-kafka-connector-X.X.X.jarファイルをKAFKA_FOLDER/libsフォルダの下に配置してください。
そして、Kafkaサーバを起動してください。

※ X.X.Xはバージョンを意味します。

configフォルダ上に、GridDB KafkaコネクタのSink connector、Source connectorを動かすためのコンフィグファイルのサンプルがあります。

## サンプルの実行(Sink connector、Source connector)

["Using Apache Kafka with GridDB database"](docs/GridDB-kafka-sink-connect-and-source-connect-guide.md)をご参照ください。

# 機能

以下のデータ型が利用可能です。
- GridDBのSTRING型, BOOL型, BYTE型, SHORT型, INTEGER型, LONG型, FLOAT型, DOUBLE型, TIMESTAMP型, BLOB型

以下のデータ型は利用できません。
- GridDbのGEOMETRY型, 配列型

## コミュニティ
  * Issues  
    質問、不具合報告はissue機能をご利用ください。
  * PullRequest  
    GridDB Contributor License Agreement(CLA_rev1.1.pdf)に同意して頂く必要があります。
    PullRequest機能をご利用の場合はGridDB Contributor License Agreementに同意したものとみなします。

# ライセンス
  
  GridDB KafkaコネクタのライセンスはApache License, version 2.0です。  
  
# 商標
  
  Apache Kafka, Kafka are either registered trademarks or trademarks of the Apache Software Foundation in the United States and/or other countries.
