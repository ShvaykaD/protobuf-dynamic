/*
 * Copyright 2015 protobuf-dynamic developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.os72.protobuf.dynamic;

import com.google.protobuf.DescriptorProtos.DescriptorProto;
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto;
import com.google.protobuf.DescriptorProtos.OneofDescriptorProto;

import java.util.HashMap;
import java.util.Map;

/**
 * MessageDefinition
 */
public class MessageDefinition {
    // --- public static ---

    public static Builder newBuilder(String msgTypeName) {
        return new Builder(msgTypeName);
    }

    // --- public ---

    public String toString() {
        return mMsgType.toString();
    }

    // --- package ---

    DescriptorProto getMessageType() {
        return mMsgType;
    }

    // --- private ---

    private MessageDefinition(DescriptorProto msgType) {
        mMsgType = msgType;
    }

    private DescriptorProto mMsgType;

    /**
     * MessageDefinition.Builder
     */
    public static class Builder {
        // --- public ---

        public Builder addField(String label, String type, String name, int num) {
            FieldDescriptorProto.Label protoLabel = sLabelMap.get(label);
            addField(protoLabel, type, name, num, null);
            return this;
        }

        public OneofBuilder addOneof(String oneofName) {
            mMsgTypeBuilder.addOneofDecl(OneofDescriptorProto.newBuilder().setName(oneofName).build());
            return new OneofBuilder(this, mOneofIndex++);
        }

        public Builder addMessageDefinition(MessageDefinition msgDef) {
            mMsgTypeBuilder.addNestedType(msgDef.getMessageType());
            return this;
        }

        public Builder addEnumDefinition(EnumDefinition enumDef) {
            mMsgTypeBuilder.addEnumType(enumDef.getEnumType());
            return this;
        }

        public MessageDefinition build() {
            return new MessageDefinition(mMsgTypeBuilder.build());
        }

        // --- private ---

        private Builder(String msgTypeName) {
            mMsgTypeBuilder = DescriptorProto.newBuilder();
            mMsgTypeBuilder.setName(msgTypeName);
        }

        private void addField(FieldDescriptorProto.Label label, String type, String name, int num, OneofBuilder oneofBuilder) {
            FieldDescriptorProto.Builder fieldBuilder = FieldDescriptorProto.newBuilder();
            if (label != null) {
                fieldBuilder.setLabel(label);
                if (FieldDescriptorProto.Label.LABEL_OPTIONAL.equals(label)) {
                    fieldBuilder.setProto3Optional(true);
                    oneofBuilder = addOneof(name);
                }
            } else {
                fieldBuilder.setLabel(FieldDescriptorProto.Label.LABEL_OPTIONAL);
            }
            FieldDescriptorProto.Type primType = sTypeMap.get(type);
            if (primType != null) {
                fieldBuilder.setType(primType);
            } else {
                fieldBuilder.setTypeName(type);
            }
            fieldBuilder.setName(name).setNumber(num);
            if (oneofBuilder != null) {
                fieldBuilder.setOneofIndex(oneofBuilder.getIdx());
            }
            mMsgTypeBuilder.addField(fieldBuilder.build());
        }

        private DescriptorProto.Builder mMsgTypeBuilder;
        private int mOneofIndex = 0;
    }

    /**
     * MessageDefinition.OneofBuilder
     */
    public static class OneofBuilder {
        // --- public ---

        public OneofBuilder addField(String type, String name, int num) {
            return add(type, name, num);
        }

        public OneofBuilder add(String type, String name, int num) {
            mMsgBuilder.addField(null, type, name, num, this);
            return this;
        }

        public MessageDefinition.Builder msgDefBuilder() {
            return mMsgBuilder;
        }

        public int getIdx() {
            return mIdx;
        }

        // --- private ---

        private OneofBuilder(MessageDefinition.Builder msgBuilder, int oneofIdx) {
            mMsgBuilder = msgBuilder;
            mIdx = oneofIdx;
        }

        private MessageDefinition.Builder mMsgBuilder;
        private int mIdx;
    }

    // --- private static ---

    private static Map<String, FieldDescriptorProto.Type> sTypeMap;
    private static Map<String, FieldDescriptorProto.Label> sLabelMap;

    static {
        sTypeMap = new HashMap<String, FieldDescriptorProto.Type>();
        sTypeMap.put("double", FieldDescriptorProto.Type.TYPE_DOUBLE);
        sTypeMap.put("float", FieldDescriptorProto.Type.TYPE_FLOAT);
        sTypeMap.put("int32", FieldDescriptorProto.Type.TYPE_INT32);
        sTypeMap.put("int64", FieldDescriptorProto.Type.TYPE_INT64);
        sTypeMap.put("uint32", FieldDescriptorProto.Type.TYPE_UINT32);
        sTypeMap.put("uint64", FieldDescriptorProto.Type.TYPE_UINT64);
        sTypeMap.put("sint32", FieldDescriptorProto.Type.TYPE_SINT32);
        sTypeMap.put("sint64", FieldDescriptorProto.Type.TYPE_SINT64);
        sTypeMap.put("fixed32", FieldDescriptorProto.Type.TYPE_FIXED32);
        sTypeMap.put("fixed64", FieldDescriptorProto.Type.TYPE_FIXED64);
        sTypeMap.put("sfixed32", FieldDescriptorProto.Type.TYPE_SFIXED32);
        sTypeMap.put("sfixed64", FieldDescriptorProto.Type.TYPE_SFIXED64);
        sTypeMap.put("bool", FieldDescriptorProto.Type.TYPE_BOOL);
        sTypeMap.put("string", FieldDescriptorProto.Type.TYPE_STRING);
        sTypeMap.put("bytes", FieldDescriptorProto.Type.TYPE_BYTES);
        //sTypeMap.put("enum", FieldDescriptorProto.Type.TYPE_ENUM);
        //sTypeMap.put("message", FieldDescriptorProto.Type.TYPE_MESSAGE);
        //sTypeMap.put("group", FieldDescriptorProto.Type.TYPE_GROUP);

        sLabelMap = new HashMap<String, FieldDescriptorProto.Label>();
        sLabelMap.put("repeated", FieldDescriptorProto.Label.LABEL_REPEATED);
        sLabelMap.put("optional", FieldDescriptorProto.Label.LABEL_OPTIONAL);
    }
}
