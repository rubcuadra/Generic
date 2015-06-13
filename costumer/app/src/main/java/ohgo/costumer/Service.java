package ohgo.costumer;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseRelation;

/**
 * Created by Ruben on 6/7/15.
 */
@ParseClassName("Service")
public class Service extends ParseObject
{
    public Service(){}

    public int getStatus() {return getNumber("status").intValue(); }
    public void setStatus(int value) {put("status", value);}

    public ParseGeoPoint getlocStart()
    { return getParseGeoPoint("locStart");}
    public ParseGeoPoint getlocEnd()
    { return getParseGeoPoint("locEnd");}

    public void setlocStart(double lat,double lng)
    {
        put("locStart",new ParseGeoPoint(lat,lng));
    }
    public void setlocEnd(double lat, double lng)
    { put("locEnd",new ParseGeoPoint(lat,lng));}

    public ParseRelation<ParseObject> getUserRelation(){ return getRelation("userId");}
    public ParseRelation<ParseObject> getEmployeeRelation(){ return getRelation("employeeId");}

    public void setUserRelation(ParseObject value){put("userId",value);}
    public void setEmployeeRelation(ParseObject value){put("employeeId",value);}
}
