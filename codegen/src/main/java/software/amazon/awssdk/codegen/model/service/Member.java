/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package software.amazon.awssdk.codegen.model.service;

public class Member {

    private String shape;

    private String location;

    private String locationName;

    private boolean payload;

    private boolean streaming;

    private boolean requiresLength;

    private String documentation;

    private String queryName;

    private boolean flattened;

    private XmlNamespace xmlNamespace;

    private boolean idempotencyToken;

    private boolean deprecated;

    private String deprecatedMessage;

    private boolean jsonvalue;

    private String timestampFormat;

    private boolean eventpayload;

    private boolean eventheader;

    private boolean endpointdiscoveryid;

    private boolean sensitive;

    private boolean xmlAttribute;

    private String deprecatedName;

    private ContextParam contextParam;

    public String getShape() {
        return shape;
    }

    public void setShape(String shape) {
        this.shape = shape;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public boolean isPayload() {
        return payload;
    }

    public void setPayload(boolean payload) {
        this.payload = payload;
    }

    public boolean isStreaming() {
        return streaming;
    }

    public void setStreaming(boolean streaming) {
        this.streaming = streaming;
    }

    public boolean isRequiresLength() {
        return requiresLength;
    }

    public void setRequiresLength(boolean requiresLength) {
        this.requiresLength = requiresLength;
    }

    public String getDocumentation() {
        return documentation;
    }

    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }

    public String getQueryName() {
        return queryName;
    }

    public void setQueryName(String queryName) {
        this.queryName = queryName;
    }

    public boolean isFlattened() {
        return flattened;
    }

    public void setFlattened(boolean flattened) {
        this.flattened = flattened;
    }

    public XmlNamespace getXmlNamespace() {
        return xmlNamespace;
    }

    public void setXmlNamespace(XmlNamespace xmlNamespace) {
        this.xmlNamespace = xmlNamespace;
    }

    public boolean isIdempotencyToken() {
        return idempotencyToken;
    }

    public void setIdempotencyToken(boolean idempotencyToken) {
        this.idempotencyToken = idempotencyToken;
    }

    public boolean isDeprecated() {
        return deprecated;
    }

    public void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }

    public String getDeprecatedMessage() {
        return deprecatedMessage;
    }

    public void setDeprecatedMessage(String deprecatedMessage) {
        this.deprecatedMessage = deprecatedMessage;
    }

    public boolean getJsonvalue() {
        return jsonvalue;
    }

    public void setJsonvalue(boolean jsonvalue) {
        this.jsonvalue = jsonvalue;
    }

    public String getTimestampFormat() {
        return timestampFormat;
    }

    public void setTimestampFormat(String timestampFormat) {
        this.timestampFormat = timestampFormat;
    }

    public boolean isEventpayload() {
        return eventpayload;
    }

    public void setEventpayload(boolean eventpayload) {
        this.eventpayload = eventpayload;
    }

    public boolean isEventheader() {
        return eventheader;
    }

    public void setEventheader(boolean eventheader) {
        this.eventheader = eventheader;
    }

    public boolean isEndpointdiscoveryid() {
        return endpointdiscoveryid;
    }

    public void setEndpointdiscoveryid(boolean endpointdiscoveryid) {
        this.endpointdiscoveryid = endpointdiscoveryid;
    }

    public boolean isSensitive() {
        return sensitive;
    }

    public void setSensitive(boolean sensitive) {
        this.sensitive = sensitive;
    }

    public boolean isXmlAttribute() {
        return xmlAttribute;
    }

    public void setXmlAttribute(boolean xmlAttribute) {
        this.xmlAttribute = xmlAttribute;
    }

    public void setDeprecatedName(String deprecatedName) {
        this.deprecatedName = deprecatedName;
    }

    public String getDeprecatedName() {
        return deprecatedName;
    }

    public ContextParam getContextParam() {
        return contextParam;
    }

    public void setContextParam(ContextParam contextParam) {
        this.contextParam = contextParam;
    }
}
