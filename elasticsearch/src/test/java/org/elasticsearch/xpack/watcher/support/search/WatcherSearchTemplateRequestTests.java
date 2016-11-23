/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License;
 * you may not use this file except in compliance with the Elastic License.
 */
package org.elasticsearch.xpack.watcher.support.search;

import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.common.ParseFieldMatcher;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentHelper;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.support.XContentMapValues;
import org.elasticsearch.search.SearchModule;
import org.elasticsearch.search.SearchRequestParsers;
import org.elasticsearch.test.ESTestCase;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import static java.util.Collections.singletonMap;
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.hamcrest.Matchers.equalTo;

public class WatcherSearchTemplateRequestTests extends ESTestCase {

    public void testFromXContentWithTemplateDefaultLang() throws IOException {
        String source = "{\"template\":{\"stored\":\"default-script\", \"params\":{\"foo\":\"bar\"}}}";
        assertTemplate(source, "default-script", "mustache", singletonMap("foo", "bar"));
    }

    public void testFromXContentWithTemplateCustomLang() throws IOException {
        String source = "{\"template\":{\"file\":\"custom-script\", \"lang\":\"painful\",\"params\":{\"bar\":\"baz\"}}}";
        assertTemplate(source, "custom-script", "painful", singletonMap("bar", "baz"));
    }

    private void assertTemplate(String source, String expectedScript, String expectedLang, Map<String, Object> expectedParams) {
        try (XContentParser parser = XContentHelper.createParser(new BytesArray(source))) {
            parser.nextToken();

            WatcherSearchTemplateRequest result = WatcherSearchTemplateRequest.fromXContent(
                    logger, parser, randomFrom(SearchType.values()), null, null);
            assertNotNull(result.getTemplate());
            assertThat(result.getTemplate().getIdOrCode(), equalTo(expectedScript));
            assertThat(result.getTemplate().getLang(), equalTo(expectedLang));
            assertThat(result.getTemplate().getParams(), equalTo(expectedParams));
        } catch (IOException e) {
            fail("Failed to parse watch search request: " + e.getMessage());
        }
    }
}
