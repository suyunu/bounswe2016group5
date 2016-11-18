package org.bounswe.digest.api.semantic;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;


public class Request {

	private static final String CONCEPTNET_URI = "http://api.conceptnet.io/";
    private static final String NBR_TO_RETRIEVE = "100";

    // Strings identifying properties in the JSON string.
    private static final String EDGES = "edges";

    // The item searched on.
    private static String input;
   
    // Data on the response for a ConceptNet lookup.
    private static ArrayList<Edge> edges = new ArrayList<Edge>();
    
    public static void main(String[] args){
    	ConceptNetQuery("galatasaray");
    	for(int i=0; i<edges.size(); i++){
    		System.out.println(edges.get(i).getRelationString());
    		System.out.println(edges.get(i).getRelationWeight() + "\n");
    	}
    }
    
    public static void ConceptNetQuery(String in)   {
        input = "/c/en/" + in;
        try {
            String qStr = CONCEPTNET_URI + input + "?limit=" + NBR_TO_RETRIEVE;
            com.mashape.unirest.http.HttpResponse<JsonNode> jb = Unirest.get(qStr)
    				.header("accept", "application/json")
    				.asJson();

    		JSONObject obj = jb.getBody().getObject();
    		// Each JSONArray element contains data on one edge of the many edges returned.
    		JSONArray resultArray = obj.getJSONArray(EDGES);

    		if(resultArray.length() != 0){
    			boolean test = true;
    			Edge edge = null;
    			for (int i = 0; i < resultArray.length(); i++) {
    				JSONObject result = resultArray.getJSONObject(i);
    				try{
    					edge = new Edge(input, result);
    				}catch(Exception e){
    					test = false;
    				}
    				if(test) edges.add(edge);
    				test = true;
    			}
    		}else{
    			qStr = CONCEPTNET_URI + "related" + input + "?limit=" + NBR_TO_RETRIEVE;
                jb = Unirest.get(qStr)
        				.header("accept", "application/json")
        				.asJson();
                
                obj = jb.getBody().getObject();
        		JSONArray json = obj.getJSONArray("related");
        		boolean test = true;
        		Edge edge = null;
        		for (int i=0; i<json.length(); i++) {
        		    JSONObject item = json.getJSONObject(i);
        		    try{
    					edge = new Edge(item.getString("@id"), input, item.getDouble("weight"));
    				}catch(Exception e){
    					test = false;
    				}
    				if(test) edges.add(edge);
    				test = true;
        		}
    		}
        } catch (UnirestException e) {
            System.out.println("UnirestException: Can't retrieve message for: " + in);
        } catch (JSONException e) {
            System.out.println("JSONException: Can't retrieve message for: " + in);
        }
        
        doInsertionSort(edges);
    }
    
    public static ArrayList<Edge> doInsertionSort(ArrayList<Edge> input){
        
        Edge temp;
        for (int i = 1; i < input.size(); i++) {
            for(int j = i ; j > 0 ; j--){
                if(input.get(j).getRelationWeight() > input.get(j-1).getRelationWeight()){
                    temp = input.get(j);
                    input.set(j, input.get(j-1));
                    input.set(j-1, temp);
                }
            }
        }
        return input;
    }
}

class Edge {
    private String lookupStr;
    Exception e;
    // Strings identifying the edge properties in the JSON string.
    private static final String RELATION = "rel";
    private static final String WEIGHT = "weight";
    private static final String SURFACE_TEXT = "surfaceText";
    private static final String START = "start";
    private static final String END = "end";
    private static final String ID = "@id";
    private static final String LABEL = "label";
    
    // This Edge's properties.
    private String relationString = "";
    private Relation relation = Relation.Other;
    private double weight = 0.0;
    private String surfaceText = "";
    private String startNode = "";
    private String startNodeId = "";
    private String endNodeId = "";
    private String endNode = "";
    private String id = "";
    private String label = "";
    private double relationWeight = 0.0;
    
    public Edge(String lookupString, JSONObject jsonObj) throws Exception  {
        lookupStr = lookupString;
        setLabel(lookupStr);
        try {
        	setId(jsonObj.getString(ID));
            setWeight(jsonObj.getDouble(WEIGHT));
            setStartNode(jsonObj.getJSONObject(START).getString(LABEL));
            startNodeId = jsonObj.getJSONObject(START).getString(ID);
            endNodeId = jsonObj.getJSONObject(END).getString(ID);
            setEndNode(jsonObj.getJSONObject(END).getString(LABEL));
            if(!jsonObj.get(SURFACE_TEXT).equals(null))
            setSurfaceText(jsonObj.getString(SURFACE_TEXT).replaceAll("(\"|\\[|\\])", ""));
            if(weight < 1) throw e;
            if(!startNodeId.startsWith("/c/en")) throw e;
            if(!endNodeId.startsWith("/c/en")) throw e;
            setRelationWeight(endNodeId, startNodeId, false);
            if(relationWeight == 0.0) throw e;
            setRelationString(startNodeId + " " + setRelation(jsonObj.getJSONObject(RELATION).getString(LABEL)) + " " + endNodeId);
        } catch (JSONException e) {
            System.out.println("JSONException in Edge constructor for string: " + e.getMessage());
        }
    }
    
    public Edge(String start, String end, double weight) throws Exception{
    	setLabel(end.substring(6));
        try {
        	setId(end);
            setStartNode(start.substring(6));
            startNodeId = start;
            endNodeId = end;
            setEndNode(end.substring(6));
            if(!startNodeId.startsWith("/c/en")) throw e;
            if(!endNodeId.startsWith("/c/en")) throw e;
            setRelationWeight(weight);
            if(relationWeight == 0.0) throw e;
            setRelationString(startNodeId + " " + setRelation(Relation.Other.toString()).toString() + " " + endNodeId);
        } catch (JSONException e) {
            System.out.println("JSONException in Edge constructor for string: " + e.getMessage());
        }
    }

    private Relation setRelation(String relationStr) {
        Relation rel = null;
        try {
            rel = Relation.valueOf(relationStr);
        } catch (IllegalArgumentException e) {
            // The relation isn't in our Relation enum. No problemówe deal with this below.
        }
        if(rel == null) {
            rel = Relation.Other;
        }
        return rel;
    }
    
    private void setRelationWeight(String end, String start, boolean cantRelate){
    	if(end.endsWith("/n")) end = end.substring(0, end.length() - 2);
    	if(start.endsWith("/n")) start = start.substring(0, start.length() - 2);
    	String qStr = "http://api.conceptnet.io/related" + end + "?filter=" + start;
    	System.out.println(qStr);
		try {
			com.mashape.unirest.http.HttpResponse<JsonNode> jb = Unirest.get(qStr)
					.header("accept", "application/json")
					.asJson();
			JSONObject obj = jb.getBody().getObject();
			try{
				if(!obj.getJSONArray("related").equals(null)){
					if(!obj.getJSONArray("related").getJSONObject(0).equals(null)){
						relationWeight = obj.getJSONArray("related").getJSONObject(0).getDouble(WEIGHT);
					}
				}
			}
			catch(JSONException e){
				if(!cantRelate){
					setRelationWeight(start, end, true);
				}
			}
		} catch (UnirestException e) {
		}
    }
    
    public double getWeight(){
    	return weight;
    }
    
    public void setWeight(double weight){
    	this.weight = weight;
    }

	public String getRelationString() {
		return relationString;
	}

	public void setRelationString(String relationString) {
		this.relationString = relationString;
	}

	public Relation getRelation() {
		return relation;
	}

	public void setRelation(Relation relation) {
		this.relation = relation;
	}

	public String getSurfaceText() {
		return surfaceText;
	}

	public void setSurfaceText(String surfaceText) {
		this.surfaceText = surfaceText;
	}

	public String getStartNode() {
		return startNode;
	}

	public void setStartNode(String startNode) {
		this.startNode = startNode;
	}

	public String getEndNode() {
		return endNode;
	}

	public void setEndNode(String endNode) {
		this.endNode = endNode;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public double getRelationWeight() {
		return relationWeight;
	}

	public void setRelationWeight(double relationWeight) {
		this.relationWeight = relationWeight;
	}
}

enum Relation {Other("is somehow related to"), 
    Antonym("is the opposite of"), NotAntonym("is not the opposite of"), 
    AtLocation("is at"), NotAtLocation("is not at"), 
    CapableOf("is capable of"), NotCapableOf("is not capable of"), 
    Causes("causes"), NotCauses("does not cause"), 
    DefinedAs("is defined as"), NotDefinedAs("is not defined as"), 
    DerivedFrom("is derived from"), NotDerivedFrom("is not derived from"),
    HasA("has a"), NotHasA("doesn't have a"), 
    HasContext("occurs in the context of"), NotHasContext("does not occur in the context of"), 
    HasPrerequisite("has a prerequisite of"), NotHasPrerequisite("does not have a prerequisite of"), 
    HasProperty("has the property of"), NotHasProperty("does not have the property of"), 
    HasSubevent("has a subevent of"), NotHasSubevent("does not have a subevent of"), 
    IsA("is a"), NotIsA("is not a"), 
    MemberOf("is a member of"), NotMemberOf("is not a member of"), 
    PartOf("is part of"), NotPartOf("is not part of"), 
    RelatedTo("is related to"), NotRelatedTo("is not related to"), 
    SimilarTo("is similar to"), NotSimilarTo("is not similar to"), 
    TranslationOf("is a translation of"), NotTranslationOf("is not a translation of"), 
    UsedFor("is used for"), NotUsedFor("is not used for");

    private String gloss;

    Relation(String str)   {
        gloss = str;
    }
    
    @Override
    public String toString()    {
        return gloss;
    }
}