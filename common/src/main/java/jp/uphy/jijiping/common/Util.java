/**
 * Copyright (C) 2013 uphy.jp
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jp.uphy.jijiping.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.List;

import com.googlecode.jcsv.reader.CSVReader;
import com.googlecode.jcsv.reader.internal.CSVReaderBuilder;
import com.googlecode.jcsv.writer.CSVWriter;
import com.googlecode.jcsv.writer.internal.CSVWriterBuilder;


/**
 * @author Yuhi Ishikura
 */
public class Util {

  private static String ENCODING = "UTF-8";

  public static String listToCsv(final List<String> list) throws IOException {
    final StringWriter sw = new StringWriter();
    final CSVWriter<String[]> csvWriter = CSVWriterBuilder.newDefaultWriter(sw);
    csvWriter.write(list.toArray(new String[0]));
    final String csv = sw.toString();
    return csv;
  }

  public static List<String> csvToList(final String csv) throws IOException {
    final CSVReader<String[]> csvReader = CSVReaderBuilder.newDefaultReader(new StringReader(csv));
    return Arrays.asList(csvReader.readNext());
  }

  /*
   * TODO 非常に非効率なので余裕があれば削除してちゃんとバッファ管理するようにする
   */

  public static String read(SocketChannel channel) throws IOException {
    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    final ByteBuffer buf = ByteBuffer.wrap(new byte[0x200]);
    int size;
    while ((size = channel.read(buf)) > 0) {
      baos.write(buf.array(), 0, size);
    }
    return new String(baos.toByteArray(), ENCODING);
  }

  public static void write(SocketChannel channel, String text) throws IOException {
    final ByteBuffer buf = ByteBuffer.wrap(text.getBytes(ENCODING));
    channel.write(buf);
  }

}
