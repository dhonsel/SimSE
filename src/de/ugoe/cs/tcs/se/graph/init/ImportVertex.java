package de.ugoe.cs.tcs.se.graph.init;

import java.util.Map;

import org.jgrapht.io.Attribute;

public class ImportVertex {
    String id;
    Map<String, Attribute> attributes;

    public ImportVertex(String id, Map<String, Attribute> attributes)
    {
        this.id = id;
        this.attributes = attributes;
    }

    public String getId()
    {
        return id;
    }

    public Map<String, Attribute> getAttributes()
    {
        return attributes;
    }

    @Override
    public String toString()
    {
        return id + ", " + attributes;
    }
}	