/*
 * Copyright (c) 2015 Cloudera, Inc.
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

package com.cloudera.nav.plugin.client.examples.extraction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import com.cloudera.nav.plugin.client.NavApiCient;
import com.cloudera.nav.plugin.client.PluginConfigurations;
import com.cloudera.nav.plugin.model.Source;
import com.cloudera.nav.plugin.model.SourceType;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.net.URL;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


/**Unit tests for IncrementalExtractorIterator
 */
@RunWith(MockitoJUnitRunner.class)
public class ExtractMetadataTest {

  private MetadataExtractor extractor;
  private String marker1Rep;

  @Before
  @SuppressWarnings("unchecked")
  public void setUp() {
    URL url = this.getClass().getClassLoader().getResource("nav_plugin.conf");
    PluginConfigurations config =new PluginConfigurations();
    config.setUsername("admin");
    config.setPassword("admin");
    config.setNavigatorUrl("navigator/url");
    NavApiCient client = mock(NavApiCient.class);

    extractor = new MetadataExtractor(client, null);
    marker1Rep = "{\"identityString\":100}";
    Source source1 = new Source("source1", SourceType.HDFS, "cluster1",
        "foo/bar", "identityString", 100);
    when(client.getAllSources()).thenReturn(Lists.newArrayList(source1));

    Map<String, String> testBody = Maps.newHashMap();
    testBody.put("query", "identity:*");
    testBody.put("cursorMark", "nextCursor");
  }

  @Test
  public void testExtractAll() {
    MetadataResultSet res = extractor.extractMetadata();
    assertTrue(res!=null);
  }

  @Test
  public void testIncrementalExtract() {
    MetadataResultSet res = extractor.extractMetadata(marker1Rep);
    assertNotNull(res);
    assertEquals(res.getMarker(), marker1Rep);
  }

  @Test
  public void testIncrementalExtractQuery(){
    String entityQuery = "sourceType:HDFS";
    String relationQuery = "type: PARENT_CHILD";
    MetadataResultSet res = extractor.extractMetadata(marker1Rep, null, entityQuery, relationQuery);
    assertNotNull(res);
    assertEquals(res.getMarker(), marker1Rep);
  }

  @Test
  public void testCurrentMarker() {
    String res = extractor.getMarker();
    assertEquals(marker1Rep, res);
  }
}

