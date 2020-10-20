package de.ugoe.cs.tcs.se.graph.init;

import java.util.Map;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.io.Attribute;

public class ImportEdge extends DefaultEdge {
    private static final long serialVersionUID = 1L;

    String id;
    Map<String, Attribute> attributes;

    public ImportEdge(String id, Map<String, Attribute> attributes)
    {
        super();
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
    public ImportVertex getTarget() {
    	return (ImportVertex) super.getTarget();
    }
    
    @Override
    public ImportVertex getSource() {
    	return (ImportVertex) super.getSource();
    }
    
    @Override
    public String toString()
    {
        return id + ", " + attributes;
    }
}
