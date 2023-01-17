// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: slop.proto

package voldemort.serialization;

public final class VSlopProto {
  private VSlopProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  public static final class Slop extends
      com.google.protobuf.GeneratedMessage {
    // Use Slop.newBuilder() to construct.
    private Slop() {
      initFields();
    }
    private Slop(boolean noInit) {}
    
    private static final Slop defaultInstance;
    public static Slop getDefaultInstance() {
      return defaultInstance;
    }
    
    public Slop getDefaultInstanceForType() {
      return defaultInstance;
    }
    
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return voldemort.serialization.VSlopProto.internal_static_voldemort_Slop_descriptor;
    }
    
    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return voldemort.serialization.VSlopProto.internal_static_voldemort_Slop_fieldAccessorTable;
    }
    
    // optional string store = 1;
    public static final int STORE_FIELD_NUMBER = 1;
    private boolean hasStore;
    private java.lang.String store_ = "";
    public boolean hasStore() { return hasStore; }
    public java.lang.String getStore() { return store_; }
    
    // optional string operation = 2;
    public static final int OPERATION_FIELD_NUMBER = 2;
    private boolean hasOperation;
    private java.lang.String operation_ = "";
    public boolean hasOperation() { return hasOperation; }
    public java.lang.String getOperation() { return operation_; }
    
    // optional bytes key = 3;
    public static final int KEY_FIELD_NUMBER = 3;
    private boolean hasKey;
    private com.google.protobuf.ByteString key_ = com.google.protobuf.ByteString.EMPTY;
    public boolean hasKey() { return hasKey; }
    public com.google.protobuf.ByteString getKey() { return key_; }
    
    // optional bytes value = 4;
    public static final int VALUE_FIELD_NUMBER = 4;
    private boolean hasValue;
    private com.google.protobuf.ByteString value_ = com.google.protobuf.ByteString.EMPTY;
    public boolean hasValue() { return hasValue; }
    public com.google.protobuf.ByteString getValue() { return value_; }
    
    // optional int32 node_id = 5;
    public static final int NODE_ID_FIELD_NUMBER = 5;
    private boolean hasNodeId;
    private int nodeId_ = 0;
    public boolean hasNodeId() { return hasNodeId; }
    public int getNodeId() { return nodeId_; }
    
    // optional int64 arrived = 6;
    public static final int ARRIVED_FIELD_NUMBER = 6;
    private boolean hasArrived;
    private long arrived_ = 0L;
    public boolean hasArrived() { return hasArrived; }
    public long getArrived() { return arrived_; }
    
    private void initFields() {
    }
    public final boolean isInitialized() {
      return true;
    }
    
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      getSerializedSize();
      if (hasStore()) {
        output.writeString(1, getStore());
      }
      if (hasOperation()) {
        output.writeString(2, getOperation());
      }
      if (hasKey()) {
        output.writeBytes(3, getKey());
      }
      if (hasValue()) {
        output.writeBytes(4, getValue());
      }
      if (hasNodeId()) {
        output.writeInt32(5, getNodeId());
      }
      if (hasArrived()) {
        output.writeInt64(6, getArrived());
      }
      getUnknownFields().writeTo(output);
    }
    
    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;
    
      size = 0;
      if (hasStore()) {
        size += com.google.protobuf.CodedOutputStream
          .computeStringSize(1, getStore());
      }
      if (hasOperation()) {
        size += com.google.protobuf.CodedOutputStream
          .computeStringSize(2, getOperation());
      }
      if (hasKey()) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(3, getKey());
      }
      if (hasValue()) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(4, getValue());
      }
      if (hasNodeId()) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(5, getNodeId());
      }
      if (hasArrived()) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt64Size(6, getArrived());
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSerializedSize = size;
      return size;
    }
    
    public static voldemort.serialization.VSlopProto.Slop parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static voldemort.serialization.VSlopProto.Slop parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static voldemort.serialization.VSlopProto.Slop parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static voldemort.serialization.VSlopProto.Slop parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static voldemort.serialization.VSlopProto.Slop parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static voldemort.serialization.VSlopProto.Slop parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    public static voldemort.serialization.VSlopProto.Slop parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static voldemort.serialization.VSlopProto.Slop parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input, extensionRegistry)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static voldemort.serialization.VSlopProto.Slop parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static voldemort.serialization.VSlopProto.Slop parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    
    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(voldemort.serialization.VSlopProto.Slop prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }
    
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder> {
      private voldemort.serialization.VSlopProto.Slop result;
      
      // Construct using voldemort.serialization.VSlopProto.Slop.newBuilder()
      private Builder() {}
      
      private static Builder create() {
        Builder builder = new Builder();
        builder.result = new voldemort.serialization.VSlopProto.Slop();
        return builder;
      }
      
      protected voldemort.serialization.VSlopProto.Slop internalGetResult() {
        return result;
      }
      
      public Builder clear() {
        if (result == null) {
          throw new IllegalStateException(
            "Cannot call clear() after build().");
        }
        result = new voldemort.serialization.VSlopProto.Slop();
        return this;
      }
      
      public Builder clone() {
        return create().mergeFrom(result);
      }
      
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return voldemort.serialization.VSlopProto.Slop.getDescriptor();
      }
      
      public voldemort.serialization.VSlopProto.Slop getDefaultInstanceForType() {
        return voldemort.serialization.VSlopProto.Slop.getDefaultInstance();
      }
      
      public boolean isInitialized() {
        return result.isInitialized();
      }
      public voldemort.serialization.VSlopProto.Slop build() {
        if (result != null && !isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return buildPartial();
      }
      
      private voldemort.serialization.VSlopProto.Slop buildParsed()
          throws com.google.protobuf.InvalidProtocolBufferException {
        if (!isInitialized()) {
          throw newUninitializedMessageException(
            result).asInvalidProtocolBufferException();
        }
        return buildPartial();
      }
      
      public voldemort.serialization.VSlopProto.Slop buildPartial() {
        if (result == null) {
          throw new IllegalStateException(
            "build() has already been called on this Builder.");
        }
        voldemort.serialization.VSlopProto.Slop returnMe = result;
        result = null;
        return returnMe;
      }
      
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof voldemort.serialization.VSlopProto.Slop) {
          return mergeFrom((voldemort.serialization.VSlopProto.Slop)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }
      
      public Builder mergeFrom(voldemort.serialization.VSlopProto.Slop other) {
        if (other == voldemort.serialization.VSlopProto.Slop.getDefaultInstance()) return this;
        if (other.hasStore()) {
          setStore(other.getStore());
        }
        if (other.hasOperation()) {
          setOperation(other.getOperation());
        }
        if (other.hasKey()) {
          setKey(other.getKey());
        }
        if (other.hasValue()) {
          setValue(other.getValue());
        }
        if (other.hasNodeId()) {
          setNodeId(other.getNodeId());
        }
        if (other.hasArrived()) {
          setArrived(other.getArrived());
        }
        this.mergeUnknownFields(other.getUnknownFields());
        return this;
      }
      
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder(
            this.getUnknownFields());
        while (true) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              this.setUnknownFields(unknownFields.build());
              return this;
            default: {
              if (!parseUnknownField(input, unknownFields,
                                     extensionRegistry, tag)) {
                this.setUnknownFields(unknownFields.build());
                return this;
              }
              break;
            }
            case 10: {
              setStore(input.readString());
              break;
            }
            case 18: {
              setOperation(input.readString());
              break;
            }
            case 26: {
              setKey(input.readBytes());
              break;
            }
            case 34: {
              setValue(input.readBytes());
              break;
            }
            case 40: {
              setNodeId(input.readInt32());
              break;
            }
            case 48: {
              setArrived(input.readInt64());
              break;
            }
          }
        }
      }
      
      
      // optional string store = 1;
      public boolean hasStore() {
        return result.hasStore();
      }
      public java.lang.String getStore() {
        return result.getStore();
      }
      public Builder setStore(java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  result.hasStore = true;
        result.store_ = value;
        return this;
      }
      public Builder clearStore() {
        result.hasStore = false;
        result.store_ = getDefaultInstance().getStore();
        return this;
      }
      
      // optional string operation = 2;
      public boolean hasOperation() {
        return result.hasOperation();
      }
      public java.lang.String getOperation() {
        return result.getOperation();
      }
      public Builder setOperation(java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  result.hasOperation = true;
        result.operation_ = value;
        return this;
      }
      public Builder clearOperation() {
        result.hasOperation = false;
        result.operation_ = getDefaultInstance().getOperation();
        return this;
      }
      
      // optional bytes key = 3;
      public boolean hasKey() {
        return result.hasKey();
      }
      public com.google.protobuf.ByteString getKey() {
        return result.getKey();
      }
      public Builder setKey(com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  result.hasKey = true;
        result.key_ = value;
        return this;
      }
      public Builder clearKey() {
        result.hasKey = false;
        result.key_ = getDefaultInstance().getKey();
        return this;
      }
      
      // optional bytes value = 4;
      public boolean hasValue() {
        return result.hasValue();
      }
      public com.google.protobuf.ByteString getValue() {
        return result.getValue();
      }
      public Builder setValue(com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  result.hasValue = true;
        result.value_ = value;
        return this;
      }
      public Builder clearValue() {
        result.hasValue = false;
        result.value_ = getDefaultInstance().getValue();
        return this;
      }
      
      // optional int32 node_id = 5;
      public boolean hasNodeId() {
        return result.hasNodeId();
      }
      public int getNodeId() {
        return result.getNodeId();
      }
      public Builder setNodeId(int value) {
        result.hasNodeId = true;
        result.nodeId_ = value;
        return this;
      }
      public Builder clearNodeId() {
        result.hasNodeId = false;
        result.nodeId_ = 0;
        return this;
      }
      
      // optional int64 arrived = 6;
      public boolean hasArrived() {
        return result.hasArrived();
      }
      public long getArrived() {
        return result.getArrived();
      }
      public Builder setArrived(long value) {
        result.hasArrived = true;
        result.arrived_ = value;
        return this;
      }
      public Builder clearArrived() {
        result.hasArrived = false;
        result.arrived_ = 0L;
        return this;
      }
      
      // @@protoc_insertion_point(builder_scope:voldemort.Slop)
    }
    
    static {
      defaultInstance = new Slop(true);
      voldemort.serialization.VSlopProto.internalForceInit();
      defaultInstance.initFields();
    }
    
    // @@protoc_insertion_point(class_scope:voldemort.Slop)
  }
  
  private static com.google.protobuf.Descriptors.Descriptor
    internal_static_voldemort_Slop_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_voldemort_Slop_fieldAccessorTable;
  
  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\nslop.proto\022\tvoldemort\"f\n\004Slop\022\r\n\005store" +
      "\030\001 \001(\t\022\021\n\toperation\030\002 \001(\t\022\013\n\003key\030\003 \001(\014\022\r" +
      "\n\005value\030\004 \001(\014\022\017\n\007node_id\030\005 \001(\005\022\017\n\007arrive" +
      "d\030\006 \001(\003B\'\n\027voldemort.serializationB\nVSlo" +
      "pProtoH\001"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
      new com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner() {
        public com.google.protobuf.ExtensionRegistry assignDescriptors(
            com.google.protobuf.Descriptors.FileDescriptor root) {
          descriptor = root;
          internal_static_voldemort_Slop_descriptor =
            getDescriptor().getMessageTypes().get(0);
          internal_static_voldemort_Slop_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessage.FieldAccessorTable(
              internal_static_voldemort_Slop_descriptor,
              new java.lang.String[] { "Store", "Operation", "Key", "Value", "NodeId", "Arrived", },
              voldemort.serialization.VSlopProto.Slop.class,
              voldemort.serialization.VSlopProto.Slop.Builder.class);
          return null;
        }
      };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
  }
  
  public static void internalForceInit() {}
  
  // @@protoc_insertion_point(outer_class_scope)
}
