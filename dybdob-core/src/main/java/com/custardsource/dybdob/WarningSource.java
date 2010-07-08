package com.custardsource.dybdob;

import javax.persistence.Embeddable;

@SuppressWarnings({"FieldCanBeLocal"})
@Embeddable
public class WarningSource {
    private final String source;
    private final String classifier;

    public WarningSource(String source, String classifier) {
        this.source = source;
        this.classifier = classifier;
    }
}
