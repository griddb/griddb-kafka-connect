/*
 * Copyright 2016 Confluent Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.griddb.kafka.connect.sink;

import org.apache.kafka.connect.sink.SinkRecord;

import java.util.List;

import com.toshiba.mwcloud.gs.GSException;

public interface BufferedRecords {

  List<SinkRecord> add(SinkRecord record) throws GSException;

  List<SinkRecord> flush() throws GSException;

  void close() throws GSException;
}
