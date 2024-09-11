package com.qesm.io;

import java.util.Map;
import org.jgrapht.nio.Attribute;

public interface DotFileConvertible {
    public Map<String, Attribute> getExporterAttributes();

    public String getExporterId();

}
