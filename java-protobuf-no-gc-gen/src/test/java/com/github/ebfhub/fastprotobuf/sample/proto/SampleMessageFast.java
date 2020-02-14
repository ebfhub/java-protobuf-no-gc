package com.github.ebfhub.fastprotobuf.sample.proto;

import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.WireFormat;

public class SampleMessageFast {
    public static class StringList implements org.ebfhub.fastprotobuf.FastProtoSetter,org.ebfhub.fastprotobuf.FastProtoWritable{
        static class FieldNum {
            static final int strings=1;
        }
        static class FieldBit {
            static final int strings=1;
        }

        public static class Field {
            public static org.ebfhub.fastprotobuf.FastProtoField strings=new org.ebfhub.fastprotobuf.FastProtoField("strings", FieldNum.strings, FieldBit.strings,WireFormat.FieldType.STRING,true,null);
        }

        @Override
        public org.ebfhub.fastprotobuf.FastProtoField field_getDef(int fieldNum){
            switch(fieldNum){
                case FieldNum.strings: return Field.strings;
                default: throw new UnsupportedOperationException();
            }
        }

        private final java.util.List<org.ebfhub.fastprotobuf.FastProtoField> field_all = java.util.Arrays.asList(Field.strings);

        @Override
        public java.util.List<org.ebfhub.fastprotobuf.FastProtoField> field_getAll(){
            return field_all;
        }


        private int fieldsSet=0;

        public boolean isSet(org.ebfhub.fastprotobuf.FastProtoField f){
            return (fieldsSet & f.bit)!=0;
        }

        public java.util.ArrayList<StringBuilder> strings;

        @Override
        public String toString(){
            StringBuilder sb = new StringBuilder();
            if((fieldsSet & FieldBit.strings)!=0) {
                if(sb.length()>0) sb.append(";");
                sb.append("strings=").append(strings);
            }
            return sb.toString();
        }
        @Override
        public void clear(org.ebfhub.fastprotobuf.FastProtoReader.ObjectPool pool){
            fieldsSet=0;
            if(this.strings!=null){
                pool.returnSpecific(this.strings);
                this.strings=null;
            }
        }
        public void write(CodedOutputStream os, org.ebfhub.fastprotobuf.FastProtoWriter writer) throws java.io.IOException {
            if((fieldsSet & FieldBit.strings)!=0) {
                for(int n=0,size=strings.size();n<size;n++){
                    writer.writeString(FieldNum.strings,os,this.strings.get(n));
                }
            }
        }

        @Override
        public org.ebfhub.fastprotobuf.FastProtoSetter field_add(int field, org.ebfhub.fastprotobuf.FastProtoReader.ObjectPool pool) {
            switch(field) {
                default: throw new UnsupportedOperationException("Unable to add");
            }
        }
        @Override
        public StringBuilder field_builder(int field, org.ebfhub.fastprotobuf.FastProtoReader.ObjectPool pool) {
            switch(field) {
                default: throw new UnsupportedOperationException("Unable to get string builder field "+field);
            }
        }
        @Override
        public void field_set(int field, double val) {
            switch(field) {
                default: throw new UnsupportedOperationException("Unable to set field "+field+" from double");
            }
        }
        @Override
        public void field_set(int field, boolean val) {
            switch(field) {
                default: throw new UnsupportedOperationException("Unable to set field "+field+" from boolean");
            }
        }
        @Override
        public void field_set(int field, int val) {
            switch(field) {
                default: throw new UnsupportedOperationException("Unable to set field "+field+" from int");
            }
        }
        @Override
        public void field_set(int field, long val) {
            switch(field) {
                default: throw new UnsupportedOperationException("Unable to set field "+field+" from long");
            }
        }
        @Override
        public void field_set(int field, float val) {
            switch(field) {
                default: throw new UnsupportedOperationException("Unable to set field "+field+" from float");
            }
        }
        public StringBuilder addStrings(org.ebfhub.fastprotobuf.FastProtoReader.ObjectPool pool) {
            if (null==strings) {
                strings=pool.takeList();
            }
            fieldsSet|=1;
            StringBuilder strings_res = pool.take(StringBuilder.class);
            strings.add(strings_res);
            return strings_res;
        }
        public int getStringsSize() {
            return strings.size();
        }

    }
    public static class FieldSetDef implements org.ebfhub.fastprotobuf.FastProtoSetter,org.ebfhub.fastprotobuf.FastProtoWritable{
        static class FieldNum {
            static final int fieldSetId=1;
            static final int fieldIds=2;
        }
        static class FieldBit {
            static final int fieldSetId=1;
            static final int fieldIds=2;
        }

        public static class Field {
            public static org.ebfhub.fastprotobuf.FastProtoField fieldSetId=new org.ebfhub.fastprotobuf.FastProtoField("fieldSetId", FieldNum.fieldSetId, FieldBit.fieldSetId,WireFormat.FieldType.INT32,false,null);
            public static org.ebfhub.fastprotobuf.FastProtoField fieldIds=new org.ebfhub.fastprotobuf.FastProtoField("fieldIds", FieldNum.fieldIds, FieldBit.fieldIds,WireFormat.FieldType.INT32,true,null);
        }

        @Override
        public org.ebfhub.fastprotobuf.FastProtoField field_getDef(int fieldNum){
            switch(fieldNum){
                case FieldNum.fieldSetId: return Field.fieldSetId;
                case FieldNum.fieldIds: return Field.fieldIds;
                default: throw new UnsupportedOperationException();
            }
        }

        private final java.util.List<org.ebfhub.fastprotobuf.FastProtoField> field_all = java.util.Arrays.asList(Field.fieldSetId, Field.fieldIds);

        @Override
        public java.util.List<org.ebfhub.fastprotobuf.FastProtoField> field_getAll(){
            return field_all;
        }


        private int fieldsSet=0;

        public boolean isSet(org.ebfhub.fastprotobuf.FastProtoField f){
            return (fieldsSet & f.bit)!=0;
        }

        public int fieldSetId;
        public gnu.trove.list.array.TIntArrayList fieldIds;

        @Override
        public String toString(){
            StringBuilder sb = new StringBuilder();
            if((fieldsSet & FieldBit.fieldSetId)!=0) {
                if(sb.length()>0) sb.append(";");
                sb.append("fieldSetId=").append(fieldSetId);
            }
            if((fieldsSet & FieldBit.fieldIds)!=0) {
                if(sb.length()>0) sb.append(";");
                sb.append("fieldIds=").append(fieldIds);
            }
            return sb.toString();
        }
        @Override
        public void clear(org.ebfhub.fastprotobuf.FastProtoReader.ObjectPool pool){
            fieldsSet=0;
            if(this.fieldIds!=null){
                pool.returnSpecific(this.fieldIds);
                this.fieldIds=null;
            }
        }
        public void write(CodedOutputStream os, org.ebfhub.fastprotobuf.FastProtoWriter writer) throws java.io.IOException {
            if((fieldsSet & FieldBit.fieldSetId)!=0) {
                os.writeSInt32(FieldNum.fieldSetId,fieldSetId);
            }
            if((fieldsSet & FieldBit.fieldIds)!=0) {
                for(int n=0,size=fieldIds.size();n<size;n++){
                    writer.writeMessage(FieldNum.fieldIds,os,this.fieldIds.get(n));
                }
            }
        }

        @Override
        public org.ebfhub.fastprotobuf.FastProtoSetter field_add(int field, org.ebfhub.fastprotobuf.FastProtoReader.ObjectPool pool) {
            switch(field) {
                default: throw new UnsupportedOperationException("Unable to add");
            }
        }
        @Override
        public StringBuilder field_builder(int field, org.ebfhub.fastprotobuf.FastProtoReader.ObjectPool pool) {
            switch(field) {
                default: throw new UnsupportedOperationException("Unable to get string builder field "+field);
            }
        }
        @Override
        public void field_set(int field, double val) {
            switch(field) {
                default: throw new UnsupportedOperationException("Unable to set field "+field+" from double");
            }
        }
        @Override
        public void field_set(int field, boolean val) {
            switch(field) {
                default: throw new UnsupportedOperationException("Unable to set field "+field+" from boolean");
            }
        }
        @Override
        public void field_set(int field, int val) {
            switch(field) {
                case FieldNum.fieldSetId:
                    this.fieldSetId=val;
                    fieldsSet|=1;
                    break;
                default: throw new UnsupportedOperationException("Unable to set field "+field+" from int");
            }
        }
        @Override
        public void field_set(int field, long val) {
            switch(field) {
                default: throw new UnsupportedOperationException("Unable to set field "+field+" from long");
            }
        }
        @Override
        public void field_set(int field, float val) {
            switch(field) {
                default: throw new UnsupportedOperationException("Unable to set field "+field+" from float");
            }
        }
        public FieldSetDef setFieldSetId(int val) {
            this.fieldSetId=val;
            fieldsSet|=1;
            return this;
        }
        public int addFieldIds(org.ebfhub.fastprotobuf.FastProtoReader.ObjectPool pool) {
            if (null==fieldIds) {
                fieldIds=pool.takeIntList();
            }
            fieldsSet|=2;
            int fieldIds_res = pool.take(int.class);
            fieldIds.add(fieldIds_res);
            return fieldIds_res;
        }
        public int getFieldIdsSize() {
            return fieldIds.size();
        }

    }
    public static class FieldIdDef implements org.ebfhub.fastprotobuf.FastProtoSetter,org.ebfhub.fastprotobuf.FastProtoWritable{
        static class FieldNum {
            static final int fieldId=1;
            static final int fieldName=2;
        }
        static class FieldBit {
            static final int fieldId=1;
            static final int fieldName=2;
        }

        public static class Field {
            public static org.ebfhub.fastprotobuf.FastProtoField fieldId=new org.ebfhub.fastprotobuf.FastProtoField("fieldId", FieldNum.fieldId, FieldBit.fieldId,WireFormat.FieldType.INT32,false,null);
            public static org.ebfhub.fastprotobuf.FastProtoField fieldName=new org.ebfhub.fastprotobuf.FastProtoField("fieldName", FieldNum.fieldName, FieldBit.fieldName,WireFormat.FieldType.STRING,false,null);
        }

        @Override
        public org.ebfhub.fastprotobuf.FastProtoField field_getDef(int fieldNum){
            switch(fieldNum){
                case FieldNum.fieldId: return Field.fieldId;
                case FieldNum.fieldName: return Field.fieldName;
                default: throw new UnsupportedOperationException();
            }
        }

        private final java.util.List<org.ebfhub.fastprotobuf.FastProtoField> field_all = java.util.Arrays.asList(Field.fieldId, Field.fieldName);

        @Override
        public java.util.List<org.ebfhub.fastprotobuf.FastProtoField> field_getAll(){
            return field_all;
        }


        private int fieldsSet=0;

        public boolean isSet(org.ebfhub.fastprotobuf.FastProtoField f){
            return (fieldsSet & f.bit)!=0;
        }

        public int fieldId;
        public StringBuilder fieldName;

        @Override
        public String toString(){
            StringBuilder sb = new StringBuilder();
            if((fieldsSet & FieldBit.fieldId)!=0) {
                if(sb.length()>0) sb.append(";");
                sb.append("fieldId=").append(fieldId);
            }
            if((fieldsSet & FieldBit.fieldName)!=0) {
                if(sb.length()>0) sb.append(";");
                sb.append("fieldName=").append(fieldName);
            }
            return sb.toString();
        }
        @Override
        public void clear(org.ebfhub.fastprotobuf.FastProtoReader.ObjectPool pool){
            fieldsSet=0;
            if(this.fieldName!=null){
                pool.returnSpecific(this.fieldName);
                this.fieldName=null;
            }
        }
        public void write(CodedOutputStream os, org.ebfhub.fastprotobuf.FastProtoWriter writer) throws java.io.IOException {
            if((fieldsSet & FieldBit.fieldId)!=0) {
                os.writeSInt32(FieldNum.fieldId,fieldId);
            }
            if((fieldsSet & FieldBit.fieldName)!=0) {
                writer.writeString(FieldNum.fieldName,os,fieldName);
            }
        }

        @Override
        public org.ebfhub.fastprotobuf.FastProtoSetter field_add(int field, org.ebfhub.fastprotobuf.FastProtoReader.ObjectPool pool) {
            switch(field) {
                default: throw new UnsupportedOperationException("Unable to add");
            }
        }
        @Override
        public StringBuilder field_builder(int field, org.ebfhub.fastprotobuf.FastProtoReader.ObjectPool pool) {
            switch(field) {
                case FieldNum.fieldName:
                    fieldsSet|=2;
                    if(this.fieldName==null) {
                        this.fieldName = pool.take(StringBuilder.class);
                    }
                    return this.fieldName;
                default: throw new UnsupportedOperationException("Unable to get string builder field "+field);
            }
        }
        @Override
        public void field_set(int field, double val) {
            switch(field) {
                default: throw new UnsupportedOperationException("Unable to set field "+field+" from double");
            }
        }
        @Override
        public void field_set(int field, boolean val) {
            switch(field) {
                default: throw new UnsupportedOperationException("Unable to set field "+field+" from boolean");
            }
        }
        @Override
        public void field_set(int field, int val) {
            switch(field) {
                case FieldNum.fieldId:
                    this.fieldId=val;
                    fieldsSet|=1;
                    break;
                default: throw new UnsupportedOperationException("Unable to set field "+field+" from int");
            }
        }
        @Override
        public void field_set(int field, long val) {
            switch(field) {
                default: throw new UnsupportedOperationException("Unable to set field "+field+" from long");
            }
        }
        @Override
        public void field_set(int field, float val) {
            switch(field) {
                default: throw new UnsupportedOperationException("Unable to set field "+field+" from float");
            }
        }
        public FieldIdDef setFieldId(int val) {
            this.fieldId=val;
            fieldsSet|=1;
            return this;
        }
        public FieldIdDef setFieldName(CharSequence val,org.ebfhub.fastprotobuf.FastProtoReader.ObjectPool pool) {
            if(this.fieldName==null) {
                this.fieldName=pool.take(StringBuilder.class);
            }
            this.fieldName.setLength(0);
            this.fieldName.append(val);
            fieldsSet|=2;
            return this;
        }

    }
    public static class FieldAndValue implements org.ebfhub.fastprotobuf.FastProtoSetter,org.ebfhub.fastprotobuf.FastProtoWritable{
        static class FieldNum {
            static final int fieldId=1;
            static final int _string=3;
            static final int _int32=4;
            static final int _int64=5;
            static final int _bool=6;
            static final int _double=7;
            static final int _float=8;
            static final int _ts=9;
            static final int _stringList=10;
        }
        static class FieldBit {
            static final int fieldId=1;
            static final int _string=2;
            static final int _int32=4;
            static final int _int64=8;
            static final int _bool=16;
            static final int _double=32;
            static final int _float=64;
            static final int _ts=128;
            static final int _stringList=256;
        }

        public static class Field {
            public static org.ebfhub.fastprotobuf.FastProtoField fieldId=new org.ebfhub.fastprotobuf.FastProtoField("fieldId", FieldNum.fieldId, FieldBit.fieldId,WireFormat.FieldType.INT32,false,null);
            public static org.ebfhub.fastprotobuf.FastProtoField _string=new org.ebfhub.fastprotobuf.FastProtoField("_string", FieldNum._string, FieldBit._string,WireFormat.FieldType.STRING,false,null);
            public static org.ebfhub.fastprotobuf.FastProtoField _int32=new org.ebfhub.fastprotobuf.FastProtoField("_int32", FieldNum._int32, FieldBit._int32,WireFormat.FieldType.INT32,false,null);
            public static org.ebfhub.fastprotobuf.FastProtoField _int64=new org.ebfhub.fastprotobuf.FastProtoField("_int64", FieldNum._int64, FieldBit._int64,WireFormat.FieldType.INT64,false,null);
            public static org.ebfhub.fastprotobuf.FastProtoField _bool=new org.ebfhub.fastprotobuf.FastProtoField("_bool", FieldNum._bool, FieldBit._bool,WireFormat.FieldType.BOOL,false,null);
            public static org.ebfhub.fastprotobuf.FastProtoField _double=new org.ebfhub.fastprotobuf.FastProtoField("_double", FieldNum._double, FieldBit._double,WireFormat.FieldType.DOUBLE,false,null);
            public static org.ebfhub.fastprotobuf.FastProtoField _float=new org.ebfhub.fastprotobuf.FastProtoField("_float", FieldNum._float, FieldBit._float,WireFormat.FieldType.FLOAT,false,null);
            public static org.ebfhub.fastprotobuf.FastProtoField _ts=new org.ebfhub.fastprotobuf.FastProtoField("_ts", FieldNum._ts, FieldBit._ts,WireFormat.FieldType.INT32,false,null);
            public static org.ebfhub.fastprotobuf.FastProtoField _stringList=new org.ebfhub.fastprotobuf.FastProtoField("_stringList", FieldNum._stringList, FieldBit._stringList,WireFormat.FieldType.MESSAGE,false,StringList.class);
        }

        @Override
        public org.ebfhub.fastprotobuf.FastProtoField field_getDef(int fieldNum){
            switch(fieldNum){
                case FieldNum.fieldId: return Field.fieldId;
                case FieldNum._string: return Field._string;
                case FieldNum._int32: return Field._int32;
                case FieldNum._int64: return Field._int64;
                case FieldNum._bool: return Field._bool;
                case FieldNum._double: return Field._double;
                case FieldNum._float: return Field._float;
                case FieldNum._ts: return Field._ts;
                case FieldNum._stringList: return Field._stringList;
                default: throw new UnsupportedOperationException();
            }
        }

        private final java.util.List<org.ebfhub.fastprotobuf.FastProtoField> field_all = java.util.Arrays.asList(Field.fieldId, Field._string, Field._int32, Field._int64, Field._bool, Field._double, Field._float, Field._ts, Field._stringList);

        @Override
        public java.util.List<org.ebfhub.fastprotobuf.FastProtoField> field_getAll(){
            return field_all;
        }

        enum OneOf_0{
            _string,
            _int32,
            _int64,
            _bool,
            _double,
            _float,
            _ts,
            _stringList,
        }
        OneOf_0 oneOf_0=null;

        private int fieldsSet=0;

        public boolean isSet(org.ebfhub.fastprotobuf.FastProtoField f){
            return (fieldsSet & f.bit)!=0;
        }

        public int fieldId;
        public StringBuilder _string;
        public int _int32;
        public long _int64;
        public boolean _bool;
        public double _double;
        public float _float;
        public int _ts;
        public StringList _stringList;

        @Override
        public String toString(){
            StringBuilder sb = new StringBuilder();
            if((fieldsSet & FieldBit.fieldId)!=0) {
                if(sb.length()>0) sb.append(";");
                sb.append("fieldId=").append(fieldId);
            }
            if((fieldsSet & FieldBit._string)!=0) {
                if(sb.length()>0) sb.append(";");
                sb.append("_string=").append(_string);
            }
            if((fieldsSet & FieldBit._int32)!=0) {
                if(sb.length()>0) sb.append(";");
                sb.append("_int32=").append(_int32);
            }
            if((fieldsSet & FieldBit._int64)!=0) {
                if(sb.length()>0) sb.append(";");
                sb.append("_int64=").append(_int64);
            }
            if((fieldsSet & FieldBit._bool)!=0) {
                if(sb.length()>0) sb.append(";");
                sb.append("_bool=").append(_bool);
            }
            if((fieldsSet & FieldBit._double)!=0) {
                if(sb.length()>0) sb.append(";");
                sb.append("_double=").append(_double);
            }
            if((fieldsSet & FieldBit._float)!=0) {
                if(sb.length()>0) sb.append(";");
                sb.append("_float=").append(_float);
            }
            if((fieldsSet & FieldBit._ts)!=0) {
                if(sb.length()>0) sb.append(";");
                sb.append("_ts=").append(_ts);
            }
            if((fieldsSet & FieldBit._stringList)!=0) {
                if(sb.length()>0) sb.append(";");
                sb.append("_stringList=").append(_stringList);
            }
            return sb.toString();
        }
        @Override
        public void clear(org.ebfhub.fastprotobuf.FastProtoReader.ObjectPool pool){
            fieldsSet=0;
            if(this._string!=null){
                pool.returnSpecific(this._string);
                this._string=null;
            }
            if(this._stringList!=null){
                pool.returnSpecific(this._stringList);
                this._stringList=null;
            }
            this.oneOf_0=null;
        }
        public void write(CodedOutputStream os, org.ebfhub.fastprotobuf.FastProtoWriter writer) throws java.io.IOException {
            if((fieldsSet & FieldBit.fieldId)!=0) {
                os.writeSInt32(FieldNum.fieldId,fieldId);
            }
            if((fieldsSet & FieldBit._string)!=0) {
                writer.writeString(FieldNum._string,os,_string);
            }
            if((fieldsSet & FieldBit._int32)!=0) {
                os.writeSInt32(FieldNum._int32,_int32);
            }
            if((fieldsSet & FieldBit._int64)!=0) {
                os.writeSInt64(FieldNum._int64,_int64);
            }
            if((fieldsSet & FieldBit._bool)!=0) {
                os.writeBool(FieldNum._bool,_bool);
            }
            if((fieldsSet & FieldBit._double)!=0) {
                os.writeDouble(FieldNum._double,_double);
            }
            if((fieldsSet & FieldBit._float)!=0) {
                os.writeFloat(FieldNum._float,_float);
            }
            if((fieldsSet & FieldBit._ts)!=0) {
                os.writeSInt32(FieldNum._ts,_ts);
            }
            if((fieldsSet & FieldBit._stringList)!=0) {
                writer.writeMessage(FieldNum._stringList,os,this._stringList);
            }
        }

        @Override
        public org.ebfhub.fastprotobuf.FastProtoSetter field_add(int field, org.ebfhub.fastprotobuf.FastProtoReader.ObjectPool pool) {
            switch(field) {
                case FieldNum._stringList:
                    if (null==_stringList) {
                        _stringList=pool.take(StringList.class);
                    }
                    fieldsSet|= FieldBit._stringList;
                    return _stringList;
                default: throw new UnsupportedOperationException("Unable to add");
            }
        }
        @Override
        public StringBuilder field_builder(int field, org.ebfhub.fastprotobuf.FastProtoReader.ObjectPool pool) {
            switch(field) {
                case FieldNum._string:
                    fieldsSet=fieldsSet& ~(FieldBit._string| FieldBit._int32| FieldBit._int64| FieldBit._bool| FieldBit._double| FieldBit._float| FieldBit._ts| FieldBit._stringList)| FieldBit._string;
                    if(this._string==null) {
                        this._string = pool.take(StringBuilder.class);
                    }
                    return this._string;
                default: throw new UnsupportedOperationException("Unable to get string builder field "+field);
            }
        }
        public void field_set(int field, StringList val) {
            switch(field) {
                case FieldNum._stringList:
                    this._stringList=val;
                    fieldsSet|=256;
                    oneOf_0= OneOf_0._stringList;
                    break;
                default: throw new UnsupportedOperationException("Unable to set field "+field+" from StringList");
            }
        }
        @Override
        public void field_set(int field, double val) {
            switch(field) {
                case FieldNum._double:
                    this._double=val;
                    fieldsSet|=32;
                    oneOf_0= OneOf_0._double;
                    break;
                default: throw new UnsupportedOperationException("Unable to set field "+field+" from double");
            }
        }
        @Override
        public void field_set(int field, boolean val) {
            switch(field) {
                case FieldNum._bool:
                    this._bool=val;
                    fieldsSet|=16;
                    oneOf_0= OneOf_0._bool;
                    break;
                default: throw new UnsupportedOperationException("Unable to set field "+field+" from boolean");
            }
        }
        @Override
        public void field_set(int field, int val) {
            switch(field) {
                case FieldNum.fieldId:
                    this.fieldId=val;
                    fieldsSet|=1;
                    break;
                case FieldNum._int32:
                    this._int32=val;
                    fieldsSet|=4;
                    oneOf_0= OneOf_0._int32;
                    break;
                case FieldNum._ts:
                    this._ts=val;
                    fieldsSet|=128;
                    oneOf_0= OneOf_0._ts;
                    break;
                default: throw new UnsupportedOperationException("Unable to set field "+field+" from int");
            }
        }
        @Override
        public void field_set(int field, long val) {
            switch(field) {
                case FieldNum._int64:
                    this._int64=val;
                    fieldsSet|=8;
                    oneOf_0= OneOf_0._int64;
                    break;
                default: throw new UnsupportedOperationException("Unable to set field "+field+" from long");
            }
        }
        @Override
        public void field_set(int field, float val) {
            switch(field) {
                case FieldNum._float:
                    this._float=val;
                    fieldsSet|=64;
                    oneOf_0= OneOf_0._float;
                    break;
                default: throw new UnsupportedOperationException("Unable to set field "+field+" from float");
            }
        }
        public FieldAndValue setFieldId(int val) {
            this.fieldId=val;
            fieldsSet|=1;
            return this;
        }
        public FieldAndValue set_string(CharSequence val,org.ebfhub.fastprotobuf.FastProtoReader.ObjectPool pool) {
            if(this._string==null) {
                this._string=pool.take(StringBuilder.class);
            }
            this._string.setLength(0);
            this._string.append(val);
            fieldsSet|=2;
            oneOf_0= OneOf_0._string;
            return this;
        }
        public FieldAndValue set_int32(int val) {
            this._int32=val;
            fieldsSet|=4;
            oneOf_0= OneOf_0._int32;
            return this;
        }
        public FieldAndValue set_int64(long val) {
            this._int64=val;
            fieldsSet|=8;
            oneOf_0= OneOf_0._int64;
            return this;
        }
        public FieldAndValue set_bool(boolean val) {
            this._bool=val;
            fieldsSet|=16;
            oneOf_0= OneOf_0._bool;
            return this;
        }
        public FieldAndValue set_double(double val) {
            this._double=val;
            fieldsSet|=32;
            oneOf_0= OneOf_0._double;
            return this;
        }
        public FieldAndValue set_float(float val) {
            this._float=val;
            fieldsSet|=64;
            oneOf_0= OneOf_0._float;
            return this;
        }
        public FieldAndValue set_ts(int val) {
            this._ts=val;
            fieldsSet|=128;
            oneOf_0= OneOf_0._ts;
            return this;
        }
        public FieldAndValue set_stringList(StringList val) {
            this._stringList=val;
            fieldsSet|=256;
            oneOf_0= OneOf_0._stringList;
            return this;
        }

    }
    public static class DataMessage implements org.ebfhub.fastprotobuf.FastProtoSetter,org.ebfhub.fastprotobuf.FastProtoWritable{
        static class FieldNum {
            static final int symbol=1;
            static final int symbolId=2;
            static final int ts=3;
            static final int smallTs=4;
            static final int fieldSetId=5;
            static final int defineFieldSet=6;
            static final int fieldIdDefs=9;
            static final int values=10;
        }
        static class FieldBit {
            static final int symbol=1;
            static final int symbolId=2;
            static final int ts=4;
            static final int smallTs=8;
            static final int fieldSetId=16;
            static final int defineFieldSet=32;
            static final int fieldIdDefs=64;
            static final int values=128;
        }

        public static class Field {
            public static org.ebfhub.fastprotobuf.FastProtoField symbol=new org.ebfhub.fastprotobuf.FastProtoField("symbol", FieldNum.symbol, FieldBit.symbol,WireFormat.FieldType.STRING,false,null);
            public static org.ebfhub.fastprotobuf.FastProtoField symbolId=new org.ebfhub.fastprotobuf.FastProtoField("symbolId", FieldNum.symbolId, FieldBit.symbolId,WireFormat.FieldType.INT32,false,null);
            public static org.ebfhub.fastprotobuf.FastProtoField ts=new org.ebfhub.fastprotobuf.FastProtoField("ts", FieldNum.ts, FieldBit.ts,WireFormat.FieldType.INT64,false,null);
            public static org.ebfhub.fastprotobuf.FastProtoField smallTs=new org.ebfhub.fastprotobuf.FastProtoField("smallTs", FieldNum.smallTs, FieldBit.smallTs,WireFormat.FieldType.INT32,false,null);
            public static org.ebfhub.fastprotobuf.FastProtoField fieldSetId=new org.ebfhub.fastprotobuf.FastProtoField("fieldSetId", FieldNum.fieldSetId, FieldBit.fieldSetId,WireFormat.FieldType.INT32,false,null);
            public static org.ebfhub.fastprotobuf.FastProtoField defineFieldSet=new org.ebfhub.fastprotobuf.FastProtoField("defineFieldSet", FieldNum.defineFieldSet, FieldBit.defineFieldSet,WireFormat.FieldType.MESSAGE,false,FieldSetDef.class);
            public static org.ebfhub.fastprotobuf.FastProtoField fieldIdDefs=new org.ebfhub.fastprotobuf.FastProtoField("fieldIdDefs", FieldNum.fieldIdDefs, FieldBit.fieldIdDefs,WireFormat.FieldType.MESSAGE,true,FieldIdDef.class);
            public static org.ebfhub.fastprotobuf.FastProtoField values=new org.ebfhub.fastprotobuf.FastProtoField("values", FieldNum.values, FieldBit.values,WireFormat.FieldType.MESSAGE,true,FieldAndValue.class);
        }

        @Override
        public org.ebfhub.fastprotobuf.FastProtoField field_getDef(int fieldNum){
            switch(fieldNum){
                case FieldNum.symbol: return Field.symbol;
                case FieldNum.symbolId: return Field.symbolId;
                case FieldNum.ts: return Field.ts;
                case FieldNum.smallTs: return Field.smallTs;
                case FieldNum.fieldSetId: return Field.fieldSetId;
                case FieldNum.defineFieldSet: return Field.defineFieldSet;
                case FieldNum.fieldIdDefs: return Field.fieldIdDefs;
                case FieldNum.values: return Field.values;
                default: throw new UnsupportedOperationException();
            }
        }

        private final java.util.List<org.ebfhub.fastprotobuf.FastProtoField> field_all = java.util.Arrays.asList(Field.symbol, Field.symbolId, Field.ts, Field.smallTs, Field.fieldSetId, Field.defineFieldSet, Field.fieldIdDefs, Field.values);

        @Override
        public java.util.List<org.ebfhub.fastprotobuf.FastProtoField> field_getAll(){
            return field_all;
        }


        private int fieldsSet=0;

        public boolean isSet(org.ebfhub.fastprotobuf.FastProtoField f){
            return (fieldsSet & f.bit)!=0;
        }

        public StringBuilder symbol;
        public int symbolId;
        public long ts;
        public int smallTs;
        public int fieldSetId;
        public FieldSetDef defineFieldSet;
        public java.util.ArrayList<FieldIdDef> fieldIdDefs;
        public java.util.ArrayList<FieldAndValue> values;

        @Override
        public String toString(){
            StringBuilder sb = new StringBuilder();
            if((fieldsSet & FieldBit.symbol)!=0) {
                if(sb.length()>0) sb.append(";");
                sb.append("symbol=").append(symbol);
            }
            if((fieldsSet & FieldBit.symbolId)!=0) {
                if(sb.length()>0) sb.append(";");
                sb.append("symbolId=").append(symbolId);
            }
            if((fieldsSet & FieldBit.ts)!=0) {
                if(sb.length()>0) sb.append(";");
                sb.append("ts=").append(ts);
            }
            if((fieldsSet & FieldBit.smallTs)!=0) {
                if(sb.length()>0) sb.append(";");
                sb.append("smallTs=").append(smallTs);
            }
            if((fieldsSet & FieldBit.fieldSetId)!=0) {
                if(sb.length()>0) sb.append(";");
                sb.append("fieldSetId=").append(fieldSetId);
            }
            if((fieldsSet & FieldBit.defineFieldSet)!=0) {
                if(sb.length()>0) sb.append(";");
                sb.append("defineFieldSet=").append(defineFieldSet);
            }
            if((fieldsSet & FieldBit.fieldIdDefs)!=0) {
                if(sb.length()>0) sb.append(";");
                sb.append("fieldIdDefs=").append(fieldIdDefs);
            }
            if((fieldsSet & FieldBit.values)!=0) {
                if(sb.length()>0) sb.append(";");
                sb.append("values=").append(values);
            }
            return sb.toString();
        }
        @Override
        public void clear(org.ebfhub.fastprotobuf.FastProtoReader.ObjectPool pool){
            fieldsSet=0;
            if(this.symbol!=null){
                pool.returnSpecific(this.symbol);
                this.symbol=null;
            }
            if(this.defineFieldSet!=null){
                pool.returnSpecific(this.defineFieldSet);
                this.defineFieldSet=null;
            }
            if(this.fieldIdDefs!=null){
                pool.returnSpecific(this.fieldIdDefs);
                this.fieldIdDefs=null;
            }
            if(this.values!=null){
                pool.returnSpecific(this.values);
                this.values=null;
            }
        }
        public void write(CodedOutputStream os, org.ebfhub.fastprotobuf.FastProtoWriter writer) throws java.io.IOException {
            if((fieldsSet & FieldBit.symbol)!=0) {
                writer.writeString(FieldNum.symbol,os,symbol);
            }
            if((fieldsSet & FieldBit.symbolId)!=0) {
                os.writeSInt32(FieldNum.symbolId,symbolId);
            }
            if((fieldsSet & FieldBit.ts)!=0) {
                os.writeSInt64(FieldNum.ts,ts);
            }
            if((fieldsSet & FieldBit.smallTs)!=0) {
                os.writeSInt32(FieldNum.smallTs,smallTs);
            }
            if((fieldsSet & FieldBit.fieldSetId)!=0) {
                os.writeSInt32(FieldNum.fieldSetId,fieldSetId);
            }
            if((fieldsSet & FieldBit.defineFieldSet)!=0) {
                writer.writeMessage(FieldNum.defineFieldSet,os,this.defineFieldSet);
            }
            if((fieldsSet & FieldBit.fieldIdDefs)!=0) {
                for(int n=0,size=fieldIdDefs.size();n<size;n++){
                    writer.writeMessage(FieldNum.fieldIdDefs,os,this.fieldIdDefs.get(n));
                }
            }
            if((fieldsSet & FieldBit.values)!=0) {
                for(int n=0,size=values.size();n<size;n++){
                    writer.writeMessage(FieldNum.values,os,this.values.get(n));
                }
            }
        }

        @Override
        public org.ebfhub.fastprotobuf.FastProtoSetter field_add(int field, org.ebfhub.fastprotobuf.FastProtoReader.ObjectPool pool) {
            switch(field) {
                case FieldNum.values:
                    if (null==values) {
                        values=pool.takeList();
                    }
                    fieldsSet|=128;
                    FieldAndValue values_res = pool.take(FieldAndValue.class);
                    values.add(values_res);
                    return values_res;
                case FieldNum.fieldIdDefs:
                    if (null==fieldIdDefs) {
                        fieldIdDefs=pool.takeList();
                    }
                    fieldsSet|=64;
                    FieldIdDef fieldIdDefs_res = pool.take(FieldIdDef.class);
                    fieldIdDefs.add(fieldIdDefs_res);
                    return fieldIdDefs_res;
                case FieldNum.defineFieldSet:
                    if (null==defineFieldSet) {
                        defineFieldSet=pool.take(FieldSetDef.class);
                    }
                    fieldsSet|= FieldBit.defineFieldSet;
                    return defineFieldSet;
                default: throw new UnsupportedOperationException("Unable to add");
            }
        }
        @Override
        public StringBuilder field_builder(int field, org.ebfhub.fastprotobuf.FastProtoReader.ObjectPool pool) {
            switch(field) {
                case FieldNum.symbol:
                    fieldsSet|=1;
                    if(this.symbol==null) {
                        this.symbol = pool.take(StringBuilder.class);
                    }
                    return this.symbol;
                default: throw new UnsupportedOperationException("Unable to get string builder field "+field);
            }
        }
        @Override
        public void field_set(int field, double val) {
            switch(field) {
                default: throw new UnsupportedOperationException("Unable to set field "+field+" from double");
            }
        }
        @Override
        public void field_set(int field, boolean val) {
            switch(field) {
                default: throw new UnsupportedOperationException("Unable to set field "+field+" from boolean");
            }
        }
        @Override
        public void field_set(int field, int val) {
            switch(field) {
                case FieldNum.symbolId:
                    this.symbolId=val;
                    fieldsSet|=2;
                    break;
                case FieldNum.smallTs:
                    this.smallTs=val;
                    fieldsSet|=8;
                    break;
                case FieldNum.fieldSetId:
                    this.fieldSetId=val;
                    fieldsSet|=16;
                    break;
                default: throw new UnsupportedOperationException("Unable to set field "+field+" from int");
            }
        }
        @Override
        public void field_set(int field, long val) {
            switch(field) {
                case FieldNum.ts:
                    this.ts=val;
                    fieldsSet|=4;
                    break;
                default: throw new UnsupportedOperationException("Unable to set field "+field+" from long");
            }
        }
        @Override
        public void field_set(int field, float val) {
            switch(field) {
                default: throw new UnsupportedOperationException("Unable to set field "+field+" from float");
            }
        }
        public void field_set(int field, FieldSetDef val) {
            switch(field) {
                case FieldNum.defineFieldSet:
                    this.defineFieldSet=val;
                    fieldsSet|=32;
                    break;
                default: throw new UnsupportedOperationException("Unable to set field "+field+" from FieldSetDef");
            }
        }
        public DataMessage setSymbol(CharSequence val,org.ebfhub.fastprotobuf.FastProtoReader.ObjectPool pool) {
            if(this.symbol==null) {
                this.symbol=pool.take(StringBuilder.class);
            }
            this.symbol.setLength(0);
            this.symbol.append(val);
            fieldsSet|=1;
            return this;
        }
        public DataMessage setSymbolId(int val) {
            this.symbolId=val;
            fieldsSet|=2;
            return this;
        }
        public DataMessage setTs(long val) {
            this.ts=val;
            fieldsSet|=4;
            return this;
        }
        public DataMessage setSmallTs(int val) {
            this.smallTs=val;
            fieldsSet|=8;
            return this;
        }
        public DataMessage setFieldSetId(int val) {
            this.fieldSetId=val;
            fieldsSet|=16;
            return this;
        }
        public DataMessage setDefineFieldSet(FieldSetDef val) {
            this.defineFieldSet=val;
            fieldsSet|=32;
            return this;
        }
        public FieldIdDef addFieldIdDefs(org.ebfhub.fastprotobuf.FastProtoReader.ObjectPool pool) {
            if (null==fieldIdDefs) {
                fieldIdDefs=pool.takeList();
            }
            fieldsSet|=64;
            FieldIdDef fieldIdDefs_res = pool.take(FieldIdDef.class);
            fieldIdDefs.add(fieldIdDefs_res);
            return fieldIdDefs_res;
        }
        public int getFieldIdDefsSize() {
            return fieldIdDefs.size();
        }
        public FieldAndValue addValues(org.ebfhub.fastprotobuf.FastProtoReader.ObjectPool pool) {
            if (null==values) {
                values=pool.takeList();
            }
            fieldsSet|=128;
            FieldAndValue values_res = pool.take(FieldAndValue.class);
            values.add(values_res);
            return values_res;
        }
        public int getValuesSize() {
            return values.size();
        }

    }
    public static class SubscriberMessagePriority implements org.ebfhub.fastprotobuf.FastProtoSetter,org.ebfhub.fastprotobuf.FastProtoWritable{
        static class FieldNum {
            static final int symbols=1;
            static final int pri=2;
            static final int until=3;
        }
        static class FieldBit {
            static final int symbols=1;
            static final int pri=2;
            static final int until=4;
        }

        public static class Field {
            public static org.ebfhub.fastprotobuf.FastProtoField symbols=new org.ebfhub.fastprotobuf.FastProtoField("symbols", FieldNum.symbols, FieldBit.symbols,WireFormat.FieldType.STRING,true,null);
            public static org.ebfhub.fastprotobuf.FastProtoField pri=new org.ebfhub.fastprotobuf.FastProtoField("pri", FieldNum.pri, FieldBit.pri,WireFormat.FieldType.INT32,false,null);
            public static org.ebfhub.fastprotobuf.FastProtoField until=new org.ebfhub.fastprotobuf.FastProtoField("until", FieldNum.until, FieldBit.until,WireFormat.FieldType.INT64,false,null);
        }

        @Override
        public org.ebfhub.fastprotobuf.FastProtoField field_getDef(int fieldNum){
            switch(fieldNum){
                case FieldNum.symbols: return Field.symbols;
                case FieldNum.pri: return Field.pri;
                case FieldNum.until: return Field.until;
                default: throw new UnsupportedOperationException();
            }
        }

        private final java.util.List<org.ebfhub.fastprotobuf.FastProtoField> field_all = java.util.Arrays.asList(Field.symbols, Field.pri, Field.until);

        @Override
        public java.util.List<org.ebfhub.fastprotobuf.FastProtoField> field_getAll(){
            return field_all;
        }


        private int fieldsSet=0;

        public boolean isSet(org.ebfhub.fastprotobuf.FastProtoField f){
            return (fieldsSet & f.bit)!=0;
        }

        public java.util.ArrayList<StringBuilder> symbols;
        public int pri;
        public long until;

        @Override
        public String toString(){
            StringBuilder sb = new StringBuilder();
            if((fieldsSet & FieldBit.symbols)!=0) {
                if(sb.length()>0) sb.append(";");
                sb.append("symbols=").append(symbols);
            }
            if((fieldsSet & FieldBit.pri)!=0) {
                if(sb.length()>0) sb.append(";");
                sb.append("pri=").append(pri);
            }
            if((fieldsSet & FieldBit.until)!=0) {
                if(sb.length()>0) sb.append(";");
                sb.append("until=").append(until);
            }
            return sb.toString();
        }
        @Override
        public void clear(org.ebfhub.fastprotobuf.FastProtoReader.ObjectPool pool){
            fieldsSet=0;
            if(this.symbols!=null){
                pool.returnSpecific(this.symbols);
                this.symbols=null;
            }
        }
        public void write(CodedOutputStream os, org.ebfhub.fastprotobuf.FastProtoWriter writer) throws java.io.IOException {
            if((fieldsSet & FieldBit.symbols)!=0) {
                for(int n=0,size=symbols.size();n<size;n++){
                    writer.writeString(FieldNum.symbols,os,this.symbols.get(n));
                }
            }
            if((fieldsSet & FieldBit.pri)!=0) {
                os.writeSInt32(FieldNum.pri,pri);
            }
            if((fieldsSet & FieldBit.until)!=0) {
                os.writeSInt64(FieldNum.until,until);
            }
        }

        @Override
        public org.ebfhub.fastprotobuf.FastProtoSetter field_add(int field, org.ebfhub.fastprotobuf.FastProtoReader.ObjectPool pool) {
            switch(field) {
                default: throw new UnsupportedOperationException("Unable to add");
            }
        }
        @Override
        public StringBuilder field_builder(int field, org.ebfhub.fastprotobuf.FastProtoReader.ObjectPool pool) {
            switch(field) {
                default: throw new UnsupportedOperationException("Unable to get string builder field "+field);
            }
        }
        @Override
        public void field_set(int field, double val) {
            switch(field) {
                default: throw new UnsupportedOperationException("Unable to set field "+field+" from double");
            }
        }
        @Override
        public void field_set(int field, boolean val) {
            switch(field) {
                default: throw new UnsupportedOperationException("Unable to set field "+field+" from boolean");
            }
        }
        @Override
        public void field_set(int field, int val) {
            switch(field) {
                case FieldNum.pri:
                    this.pri=val;
                    fieldsSet|=2;
                    break;
                default: throw new UnsupportedOperationException("Unable to set field "+field+" from int");
            }
        }
        @Override
        public void field_set(int field, long val) {
            switch(field) {
                case FieldNum.until:
                    this.until=val;
                    fieldsSet|=4;
                    break;
                default: throw new UnsupportedOperationException("Unable to set field "+field+" from long");
            }
        }
        @Override
        public void field_set(int field, float val) {
            switch(field) {
                default: throw new UnsupportedOperationException("Unable to set field "+field+" from float");
            }
        }
        public StringBuilder addSymbols(org.ebfhub.fastprotobuf.FastProtoReader.ObjectPool pool) {
            if (null==symbols) {
                symbols=pool.takeList();
            }
            fieldsSet|=1;
            StringBuilder symbols_res = pool.take(StringBuilder.class);
            symbols.add(symbols_res);
            return symbols_res;
        }
        public int getSymbolsSize() {
            return symbols.size();
        }
        public SubscriberMessagePriority setPri(int val) {
            this.pri=val;
            fieldsSet|=2;
            return this;
        }
        public SubscriberMessagePriority setUntil(long val) {
            this.until=val;
            fieldsSet|=4;
            return this;
        }

    }
    public static class SubscriberMessageSubscribe implements org.ebfhub.fastprotobuf.FastProtoSetter,org.ebfhub.fastprotobuf.FastProtoWritable{
        static class FieldNum {
            static final int symbols=1;
            static final int pri=2;
            static final int until=3;
        }
        static class FieldBit {
            static final int symbols=1;
            static final int pri=2;
            static final int until=4;
        }

        public static class Field {
            public static org.ebfhub.fastprotobuf.FastProtoField symbols=new org.ebfhub.fastprotobuf.FastProtoField("symbols", FieldNum.symbols, FieldBit.symbols,WireFormat.FieldType.STRING,true,null);
            public static org.ebfhub.fastprotobuf.FastProtoField pri=new org.ebfhub.fastprotobuf.FastProtoField("pri", FieldNum.pri, FieldBit.pri,WireFormat.FieldType.INT32,false,null);
            public static org.ebfhub.fastprotobuf.FastProtoField until=new org.ebfhub.fastprotobuf.FastProtoField("until", FieldNum.until, FieldBit.until,WireFormat.FieldType.INT64,false,null);
        }

        @Override
        public org.ebfhub.fastprotobuf.FastProtoField field_getDef(int fieldNum){
            switch(fieldNum){
                case FieldNum.symbols: return Field.symbols;
                case FieldNum.pri: return Field.pri;
                case FieldNum.until: return Field.until;
                default: throw new UnsupportedOperationException();
            }
        }

        private final java.util.List<org.ebfhub.fastprotobuf.FastProtoField> field_all = java.util.Arrays.asList(Field.symbols, Field.pri, Field.until);

        @Override
        public java.util.List<org.ebfhub.fastprotobuf.FastProtoField> field_getAll(){
            return field_all;
        }


        private int fieldsSet=0;

        public boolean isSet(org.ebfhub.fastprotobuf.FastProtoField f){
            return (fieldsSet & f.bit)!=0;
        }

        public java.util.ArrayList<StringBuilder> symbols;
        public int pri;
        public long until;

        @Override
        public String toString(){
            StringBuilder sb = new StringBuilder();
            if((fieldsSet & FieldBit.symbols)!=0) {
                if(sb.length()>0) sb.append(";");
                sb.append("symbols=").append(symbols);
            }
            if((fieldsSet & FieldBit.pri)!=0) {
                if(sb.length()>0) sb.append(";");
                sb.append("pri=").append(pri);
            }
            if((fieldsSet & FieldBit.until)!=0) {
                if(sb.length()>0) sb.append(";");
                sb.append("until=").append(until);
            }
            return sb.toString();
        }
        @Override
        public void clear(org.ebfhub.fastprotobuf.FastProtoReader.ObjectPool pool){
            fieldsSet=0;
            if(this.symbols!=null){
                pool.returnSpecific(this.symbols);
                this.symbols=null;
            }
        }
        public void write(CodedOutputStream os, org.ebfhub.fastprotobuf.FastProtoWriter writer) throws java.io.IOException {
            if((fieldsSet & FieldBit.symbols)!=0) {
                for(int n=0,size=symbols.size();n<size;n++){
                    writer.writeString(FieldNum.symbols,os,this.symbols.get(n));
                }
            }
            if((fieldsSet & FieldBit.pri)!=0) {
                os.writeSInt32(FieldNum.pri,pri);
            }
            if((fieldsSet & FieldBit.until)!=0) {
                os.writeSInt64(FieldNum.until,until);
            }
        }

        @Override
        public org.ebfhub.fastprotobuf.FastProtoSetter field_add(int field, org.ebfhub.fastprotobuf.FastProtoReader.ObjectPool pool) {
            switch(field) {
                default: throw new UnsupportedOperationException("Unable to add");
            }
        }
        @Override
        public StringBuilder field_builder(int field, org.ebfhub.fastprotobuf.FastProtoReader.ObjectPool pool) {
            switch(field) {
                default: throw new UnsupportedOperationException("Unable to get string builder field "+field);
            }
        }
        @Override
        public void field_set(int field, double val) {
            switch(field) {
                default: throw new UnsupportedOperationException("Unable to set field "+field+" from double");
            }
        }
        @Override
        public void field_set(int field, boolean val) {
            switch(field) {
                default: throw new UnsupportedOperationException("Unable to set field "+field+" from boolean");
            }
        }
        @Override
        public void field_set(int field, int val) {
            switch(field) {
                case FieldNum.pri:
                    this.pri=val;
                    fieldsSet|=2;
                    break;
                default: throw new UnsupportedOperationException("Unable to set field "+field+" from int");
            }
        }
        @Override
        public void field_set(int field, long val) {
            switch(field) {
                case FieldNum.until:
                    this.until=val;
                    fieldsSet|=4;
                    break;
                default: throw new UnsupportedOperationException("Unable to set field "+field+" from long");
            }
        }
        @Override
        public void field_set(int field, float val) {
            switch(field) {
                default: throw new UnsupportedOperationException("Unable to set field "+field+" from float");
            }
        }
        public StringBuilder addSymbols(org.ebfhub.fastprotobuf.FastProtoReader.ObjectPool pool) {
            if (null==symbols) {
                symbols=pool.takeList();
            }
            fieldsSet|=1;
            StringBuilder symbols_res = pool.take(StringBuilder.class);
            symbols.add(symbols_res);
            return symbols_res;
        }
        public int getSymbolsSize() {
            return symbols.size();
        }
        public SubscriberMessageSubscribe setPri(int val) {
            this.pri=val;
            fieldsSet|=2;
            return this;
        }
        public SubscriberMessageSubscribe setUntil(long val) {
            this.until=val;
            fieldsSet|=4;
            return this;
        }

    }
    public static class SubscriberMessageQueueRate implements org.ebfhub.fastprotobuf.FastProtoSetter,org.ebfhub.fastprotobuf.FastProtoWritable{
        static class FieldNum {
            static final int messagesPerSec=1;
        }
        static class FieldBit {
            static final int messagesPerSec=1;
        }

        public static class Field {
            public static org.ebfhub.fastprotobuf.FastProtoField messagesPerSec=new org.ebfhub.fastprotobuf.FastProtoField("messagesPerSec", FieldNum.messagesPerSec, FieldBit.messagesPerSec,WireFormat.FieldType.DOUBLE,false,null);
        }

        @Override
        public org.ebfhub.fastprotobuf.FastProtoField field_getDef(int fieldNum){
            switch(fieldNum){
                case FieldNum.messagesPerSec: return Field.messagesPerSec;
                default: throw new UnsupportedOperationException();
            }
        }

        private final java.util.List<org.ebfhub.fastprotobuf.FastProtoField> field_all = java.util.Arrays.asList(Field.messagesPerSec);

        @Override
        public java.util.List<org.ebfhub.fastprotobuf.FastProtoField> field_getAll(){
            return field_all;
        }


        private int fieldsSet=0;

        public boolean isSet(org.ebfhub.fastprotobuf.FastProtoField f){
            return (fieldsSet & f.bit)!=0;
        }

        public double messagesPerSec;

        @Override
        public String toString(){
            StringBuilder sb = new StringBuilder();
            if((fieldsSet & FieldBit.messagesPerSec)!=0) {
                if(sb.length()>0) sb.append(";");
                sb.append("messagesPerSec=").append(messagesPerSec);
            }
            return sb.toString();
        }
        @Override
        public void clear(org.ebfhub.fastprotobuf.FastProtoReader.ObjectPool pool){
            fieldsSet=0;
        }
        public void write(CodedOutputStream os, org.ebfhub.fastprotobuf.FastProtoWriter writer) throws java.io.IOException {
            if((fieldsSet & FieldBit.messagesPerSec)!=0) {
                os.writeDouble(FieldNum.messagesPerSec,messagesPerSec);
            }
        }

        @Override
        public org.ebfhub.fastprotobuf.FastProtoSetter field_add(int field, org.ebfhub.fastprotobuf.FastProtoReader.ObjectPool pool) {
            switch(field) {
                default: throw new UnsupportedOperationException("Unable to add");
            }
        }
        @Override
        public StringBuilder field_builder(int field, org.ebfhub.fastprotobuf.FastProtoReader.ObjectPool pool) {
            switch(field) {
                default: throw new UnsupportedOperationException("Unable to get string builder field "+field);
            }
        }
        @Override
        public void field_set(int field, double val) {
            switch(field) {
                case FieldNum.messagesPerSec:
                    this.messagesPerSec=val;
                    fieldsSet|=1;
                    break;
                default: throw new UnsupportedOperationException("Unable to set field "+field+" from double");
            }
        }
        @Override
        public void field_set(int field, boolean val) {
            switch(field) {
                default: throw new UnsupportedOperationException("Unable to set field "+field+" from boolean");
            }
        }
        @Override
        public void field_set(int field, int val) {
            switch(field) {
                default: throw new UnsupportedOperationException("Unable to set field "+field+" from int");
            }
        }
        @Override
        public void field_set(int field, long val) {
            switch(field) {
                default: throw new UnsupportedOperationException("Unable to set field "+field+" from long");
            }
        }
        @Override
        public void field_set(int field, float val) {
            switch(field) {
                default: throw new UnsupportedOperationException("Unable to set field "+field+" from float");
            }
        }
        public SubscriberMessageQueueRate setMessagesPerSec(double val) {
            this.messagesPerSec=val;
            fieldsSet|=1;
            return this;
        }

    }
    public static class SubscriberMessageFlow implements org.ebfhub.fastprotobuf.FastProtoSetter,org.ebfhub.fastprotobuf.FastProtoWritable{
        static class FieldNum {
            static final int bytes=1;
        }
        static class FieldBit {
            static final int bytes=1;
        }

        public static class Field {
            public static org.ebfhub.fastprotobuf.FastProtoField bytes=new org.ebfhub.fastprotobuf.FastProtoField("bytes", FieldNum.bytes, FieldBit.bytes,WireFormat.FieldType.INT32,false,null);
        }

        @Override
        public org.ebfhub.fastprotobuf.FastProtoField field_getDef(int fieldNum){
            switch(fieldNum){
                case FieldNum.bytes: return Field.bytes;
                default: throw new UnsupportedOperationException();
            }
        }

        private final java.util.List<org.ebfhub.fastprotobuf.FastProtoField> field_all = java.util.Arrays.asList(Field.bytes);

        @Override
        public java.util.List<org.ebfhub.fastprotobuf.FastProtoField> field_getAll(){
            return field_all;
        }


        private int fieldsSet=0;

        public boolean isSet(org.ebfhub.fastprotobuf.FastProtoField f){
            return (fieldsSet & f.bit)!=0;
        }

        public int bytes;

        @Override
        public String toString(){
            StringBuilder sb = new StringBuilder();
            if((fieldsSet & FieldBit.bytes)!=0) {
                if(sb.length()>0) sb.append(";");
                sb.append("bytes=").append(bytes);
            }
            return sb.toString();
        }
        @Override
        public void clear(org.ebfhub.fastprotobuf.FastProtoReader.ObjectPool pool){
            fieldsSet=0;
        }
        public void write(CodedOutputStream os, org.ebfhub.fastprotobuf.FastProtoWriter writer) throws java.io.IOException {
            if((fieldsSet & FieldBit.bytes)!=0) {
                os.writeSInt32(FieldNum.bytes,bytes);
            }
        }

        @Override
        public org.ebfhub.fastprotobuf.FastProtoSetter field_add(int field, org.ebfhub.fastprotobuf.FastProtoReader.ObjectPool pool) {
            switch(field) {
                default: throw new UnsupportedOperationException("Unable to add");
            }
        }
        @Override
        public StringBuilder field_builder(int field, org.ebfhub.fastprotobuf.FastProtoReader.ObjectPool pool) {
            switch(field) {
                default: throw new UnsupportedOperationException("Unable to get string builder field "+field);
            }
        }
        @Override
        public void field_set(int field, double val) {
            switch(field) {
                default: throw new UnsupportedOperationException("Unable to set field "+field+" from double");
            }
        }
        @Override
        public void field_set(int field, boolean val) {
            switch(field) {
                default: throw new UnsupportedOperationException("Unable to set field "+field+" from boolean");
            }
        }
        @Override
        public void field_set(int field, int val) {
            switch(field) {
                case FieldNum.bytes:
                    this.bytes=val;
                    fieldsSet|=1;
                    break;
                default: throw new UnsupportedOperationException("Unable to set field "+field+" from int");
            }
        }
        @Override
        public void field_set(int field, long val) {
            switch(field) {
                default: throw new UnsupportedOperationException("Unable to set field "+field+" from long");
            }
        }
        @Override
        public void field_set(int field, float val) {
            switch(field) {
                default: throw new UnsupportedOperationException("Unable to set field "+field+" from float");
            }
        }
        public SubscriberMessageFlow setBytes(int val) {
            this.bytes=val;
            fieldsSet|=1;
            return this;
        }

    }
    public static class SubscriberMessagePart implements org.ebfhub.fastprotobuf.FastProtoSetter,org.ebfhub.fastprotobuf.FastProtoWritable{
        static class FieldNum {
            static final int priority=1;
            static final int subscribe=2;
            static final int queueRate=3;
            static final int flow=4;
        }
        static class FieldBit {
            static final int priority=1;
            static final int subscribe=2;
            static final int queueRate=4;
            static final int flow=8;
        }

        public static class Field {
            public static org.ebfhub.fastprotobuf.FastProtoField priority=new org.ebfhub.fastprotobuf.FastProtoField("priority", FieldNum.priority, FieldBit.priority,WireFormat.FieldType.MESSAGE,false,SubscriberMessagePriority.class);
            public static org.ebfhub.fastprotobuf.FastProtoField subscribe=new org.ebfhub.fastprotobuf.FastProtoField("subscribe", FieldNum.subscribe, FieldBit.subscribe,WireFormat.FieldType.MESSAGE,false,SubscriberMessageSubscribe.class);
            public static org.ebfhub.fastprotobuf.FastProtoField queueRate=new org.ebfhub.fastprotobuf.FastProtoField("queueRate", FieldNum.queueRate, FieldBit.queueRate,WireFormat.FieldType.MESSAGE,false,SubscriberMessageQueueRate.class);
            public static org.ebfhub.fastprotobuf.FastProtoField flow=new org.ebfhub.fastprotobuf.FastProtoField("flow", FieldNum.flow, FieldBit.flow,WireFormat.FieldType.MESSAGE,false,SubscriberMessageFlow.class);
        }

        @Override
        public org.ebfhub.fastprotobuf.FastProtoField field_getDef(int fieldNum){
            switch(fieldNum){
                case FieldNum.priority: return Field.priority;
                case FieldNum.subscribe: return Field.subscribe;
                case FieldNum.queueRate: return Field.queueRate;
                case FieldNum.flow: return Field.flow;
                default: throw new UnsupportedOperationException();
            }
        }

        private final java.util.List<org.ebfhub.fastprotobuf.FastProtoField> field_all = java.util.Arrays.asList(Field.priority, Field.subscribe, Field.queueRate, Field.flow);

        @Override
        public java.util.List<org.ebfhub.fastprotobuf.FastProtoField> field_getAll(){
            return field_all;
        }

        enum OneOf_0{
            priority,
            subscribe,
            queueRate,
            flow,
        }
        OneOf_0 oneOf_0=null;

        private int fieldsSet=0;

        public boolean isSet(org.ebfhub.fastprotobuf.FastProtoField f){
            return (fieldsSet & f.bit)!=0;
        }

        public SubscriberMessagePriority priority;
        public SubscriberMessageSubscribe subscribe;
        public SubscriberMessageQueueRate queueRate;
        public SubscriberMessageFlow flow;

        @Override
        public String toString(){
            StringBuilder sb = new StringBuilder();
            if((fieldsSet & FieldBit.priority)!=0) {
                if(sb.length()>0) sb.append(";");
                sb.append("priority=").append(priority);
            }
            if((fieldsSet & FieldBit.subscribe)!=0) {
                if(sb.length()>0) sb.append(";");
                sb.append("subscribe=").append(subscribe);
            }
            if((fieldsSet & FieldBit.queueRate)!=0) {
                if(sb.length()>0) sb.append(";");
                sb.append("queueRate=").append(queueRate);
            }
            if((fieldsSet & FieldBit.flow)!=0) {
                if(sb.length()>0) sb.append(";");
                sb.append("flow=").append(flow);
            }
            return sb.toString();
        }
        @Override
        public void clear(org.ebfhub.fastprotobuf.FastProtoReader.ObjectPool pool){
            fieldsSet=0;
            if(this.priority!=null){
                pool.returnSpecific(this.priority);
                this.priority=null;
            }
            if(this.subscribe!=null){
                pool.returnSpecific(this.subscribe);
                this.subscribe=null;
            }
            if(this.queueRate!=null){
                pool.returnSpecific(this.queueRate);
                this.queueRate=null;
            }
            if(this.flow!=null){
                pool.returnSpecific(this.flow);
                this.flow=null;
            }
            this.oneOf_0=null;
        }
        public void write(CodedOutputStream os, org.ebfhub.fastprotobuf.FastProtoWriter writer) throws java.io.IOException {
            if((fieldsSet & FieldBit.priority)!=0) {
                writer.writeMessage(FieldNum.priority,os,this.priority);
            }
            if((fieldsSet & FieldBit.subscribe)!=0) {
                writer.writeMessage(FieldNum.subscribe,os,this.subscribe);
            }
            if((fieldsSet & FieldBit.queueRate)!=0) {
                writer.writeMessage(FieldNum.queueRate,os,this.queueRate);
            }
            if((fieldsSet & FieldBit.flow)!=0) {
                writer.writeMessage(FieldNum.flow,os,this.flow);
            }
        }

        @Override
        public org.ebfhub.fastprotobuf.FastProtoSetter field_add(int field, org.ebfhub.fastprotobuf.FastProtoReader.ObjectPool pool) {
            switch(field) {
                case FieldNum.queueRate:
                    if (null==queueRate) {
                        queueRate=pool.take(SubscriberMessageQueueRate.class);
                    }
                    fieldsSet|= FieldBit.queueRate;
                    return queueRate;
                case FieldNum.subscribe:
                    if (null==subscribe) {
                        subscribe=pool.take(SubscriberMessageSubscribe.class);
                    }
                    fieldsSet|= FieldBit.subscribe;
                    return subscribe;
                case FieldNum.priority:
                    if (null==priority) {
                        priority=pool.take(SubscriberMessagePriority.class);
                    }
                    fieldsSet|= FieldBit.priority;
                    return priority;
                case FieldNum.flow:
                    if (null==flow) {
                        flow=pool.take(SubscriberMessageFlow.class);
                    }
                    fieldsSet|= FieldBit.flow;
                    return flow;
                default: throw new UnsupportedOperationException("Unable to add");
            }
        }
        public void field_set(int field, SubscriberMessageQueueRate val) {
            switch(field) {
                case FieldNum.queueRate:
                    this.queueRate=val;
                    fieldsSet|=4;
                    oneOf_0= OneOf_0.queueRate;
                    break;
                default: throw new UnsupportedOperationException("Unable to set field "+field+" from SubscriberMessageQueueRate");
            }
        }
        @Override
        public StringBuilder field_builder(int field, org.ebfhub.fastprotobuf.FastProtoReader.ObjectPool pool) {
            switch(field) {
                default: throw new UnsupportedOperationException("Unable to get string builder field "+field);
            }
        }
        @Override
        public void field_set(int field, double val) {
            switch(field) {
                default: throw new UnsupportedOperationException("Unable to set field "+field+" from double");
            }
        }
        public void field_set(int field, SubscriberMessageSubscribe val) {
            switch(field) {
                case FieldNum.subscribe:
                    this.subscribe=val;
                    fieldsSet|=2;
                    oneOf_0= OneOf_0.subscribe;
                    break;
                default: throw new UnsupportedOperationException("Unable to set field "+field+" from SubscriberMessageSubscribe");
            }
        }
        public void field_set(int field, SubscriberMessagePriority val) {
            switch(field) {
                case FieldNum.priority:
                    this.priority=val;
                    fieldsSet|=1;
                    oneOf_0= OneOf_0.priority;
                    break;
                default: throw new UnsupportedOperationException("Unable to set field "+field+" from SubscriberMessagePriority");
            }
        }
        public void field_set(int field, SubscriberMessageFlow val) {
            switch(field) {
                case FieldNum.flow:
                    this.flow=val;
                    fieldsSet|=8;
                    oneOf_0= OneOf_0.flow;
                    break;
                default: throw new UnsupportedOperationException("Unable to set field "+field+" from SubscriberMessageFlow");
            }
        }
        @Override
        public void field_set(int field, boolean val) {
            switch(field) {
                default: throw new UnsupportedOperationException("Unable to set field "+field+" from boolean");
            }
        }
        @Override
        public void field_set(int field, int val) {
            switch(field) {
                default: throw new UnsupportedOperationException("Unable to set field "+field+" from int");
            }
        }
        @Override
        public void field_set(int field, long val) {
            switch(field) {
                default: throw new UnsupportedOperationException("Unable to set field "+field+" from long");
            }
        }
        @Override
        public void field_set(int field, float val) {
            switch(field) {
                default: throw new UnsupportedOperationException("Unable to set field "+field+" from float");
            }
        }
        public SubscriberMessagePart setPriority(SubscriberMessagePriority val) {
            this.priority=val;
            fieldsSet|=1;
            oneOf_0= OneOf_0.priority;
            return this;
        }
        public SubscriberMessagePart setSubscribe(SubscriberMessageSubscribe val) {
            this.subscribe=val;
            fieldsSet|=2;
            oneOf_0= OneOf_0.subscribe;
            return this;
        }
        public SubscriberMessagePart setQueueRate(SubscriberMessageQueueRate val) {
            this.queueRate=val;
            fieldsSet|=4;
            oneOf_0= OneOf_0.queueRate;
            return this;
        }
        public SubscriberMessagePart setFlow(SubscriberMessageFlow val) {
            this.flow=val;
            fieldsSet|=8;
            oneOf_0= OneOf_0.flow;
            return this;
        }

    }
    public static class SubscriberMessage implements org.ebfhub.fastprotobuf.FastProtoSetter,org.ebfhub.fastprotobuf.FastProtoWritable{
        static class FieldNum {
            static final int messageParts=1;
        }
        static class FieldBit {
            static final int messageParts=1;
        }

        public static class Field {
            public static org.ebfhub.fastprotobuf.FastProtoField messageParts=new org.ebfhub.fastprotobuf.FastProtoField("messageParts", FieldNum.messageParts, FieldBit.messageParts,WireFormat.FieldType.MESSAGE,true,SubscriberMessagePart.class);
        }

        @Override
        public org.ebfhub.fastprotobuf.FastProtoField field_getDef(int fieldNum){
            switch(fieldNum){
                case FieldNum.messageParts: return Field.messageParts;
                default: throw new UnsupportedOperationException();
            }
        }

        private final java.util.List<org.ebfhub.fastprotobuf.FastProtoField> field_all = java.util.Arrays.asList(Field.messageParts);

        @Override
        public java.util.List<org.ebfhub.fastprotobuf.FastProtoField> field_getAll(){
            return field_all;
        }


        private int fieldsSet=0;

        public boolean isSet(org.ebfhub.fastprotobuf.FastProtoField f){
            return (fieldsSet & f.bit)!=0;
        }

        public java.util.ArrayList<SubscriberMessagePart> messageParts;

        @Override
        public String toString(){
            StringBuilder sb = new StringBuilder();
            if((fieldsSet & FieldBit.messageParts)!=0) {
                if(sb.length()>0) sb.append(";");
                sb.append("messageParts=").append(messageParts);
            }
            return sb.toString();
        }
        @Override
        public void clear(org.ebfhub.fastprotobuf.FastProtoReader.ObjectPool pool){
            fieldsSet=0;
            if(this.messageParts!=null){
                pool.returnSpecific(this.messageParts);
                this.messageParts=null;
            }
        }
        public void write(CodedOutputStream os, org.ebfhub.fastprotobuf.FastProtoWriter writer) throws java.io.IOException {
            if((fieldsSet & FieldBit.messageParts)!=0) {
                for(int n=0,size=messageParts.size();n<size;n++){
                    writer.writeMessage(FieldNum.messageParts,os,this.messageParts.get(n));
                }
            }
        }

        @Override
        public org.ebfhub.fastprotobuf.FastProtoSetter field_add(int field, org.ebfhub.fastprotobuf.FastProtoReader.ObjectPool pool) {
            switch(field) {
                case FieldNum.messageParts:
                    if (null==messageParts) {
                        messageParts=pool.takeList();
                    }
                    fieldsSet|=1;
                    SubscriberMessagePart messageParts_res = pool.take(SubscriberMessagePart.class);
                    messageParts.add(messageParts_res);
                    return messageParts_res;
                default: throw new UnsupportedOperationException("Unable to add");
            }
        }
        @Override
        public StringBuilder field_builder(int field, org.ebfhub.fastprotobuf.FastProtoReader.ObjectPool pool) {
            switch(field) {
                default: throw new UnsupportedOperationException("Unable to get string builder field "+field);
            }
        }
        @Override
        public void field_set(int field, double val) {
            switch(field) {
                default: throw new UnsupportedOperationException("Unable to set field "+field+" from double");
            }
        }
        @Override
        public void field_set(int field, boolean val) {
            switch(field) {
                default: throw new UnsupportedOperationException("Unable to set field "+field+" from boolean");
            }
        }
        @Override
        public void field_set(int field, int val) {
            switch(field) {
                default: throw new UnsupportedOperationException("Unable to set field "+field+" from int");
            }
        }
        @Override
        public void field_set(int field, long val) {
            switch(field) {
                default: throw new UnsupportedOperationException("Unable to set field "+field+" from long");
            }
        }
        @Override
        public void field_set(int field, float val) {
            switch(field) {
                default: throw new UnsupportedOperationException("Unable to set field "+field+" from float");
            }
        }
        public SubscriberMessagePart addMessageParts(org.ebfhub.fastprotobuf.FastProtoReader.ObjectPool pool) {
            if (null==messageParts) {
                messageParts=pool.takeList();
            }
            fieldsSet|=1;
            SubscriberMessagePart messageParts_res = pool.take(SubscriberMessagePart.class);
            messageParts.add(messageParts_res);
            return messageParts_res;
        }
        public int getMessagePartsSize() {
            return messageParts.size();
        }

    }
}
