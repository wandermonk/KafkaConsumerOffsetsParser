package com.monitoring.kafka;

public class ConsumerOffsetDetails {

    private String topic;
    private Integer partition;
    private String group;
    private Short version;
    private Long offset;
    private String metadata;
    private Long commitTimestamp;
    private Long expireTimestamp;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Integer getPartition() {
        return partition;
    }

    public void setPartition(Integer partition) {
        this.partition = partition;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Short getVersion() {
        return version;
    }

    public void setVersion(Short version) {
        this.version = version;
    }

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public Long getCommitTimestamp() {
        return commitTimestamp;
    }

    public void setCommitTimestamp(Long commitTimestamp) {
        this.commitTimestamp = commitTimestamp;
    }

    public Long getExpireTimestamp() {
        return expireTimestamp;
    }

    public void setExpireTimestamp(Long expireTimestamp) {
        this.expireTimestamp = expireTimestamp;
    }
}
