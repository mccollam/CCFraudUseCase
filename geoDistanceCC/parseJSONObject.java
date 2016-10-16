package geoDistanceCC;


import java.io.BufferedReader;
//import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.JSONException;
import org.json.JSONObject;

 
/**
 * @author eran orgad
 *   Hortonworks 04/27/2016
 *   
 *    From Nifi process we execute the Python script and parse to match records
 *    if we find a match we calculate the distance and enrich the results back to our dynamic Web UI for presentation
 * 
 */

public class parseJSONObject {
	
	
	public static int executePython() throws IOException, JSONException {
		
		String jsonData = "";
		String comma = ",";
		String squarebrq = "[";
		String squarebrqend = "]";
		int retunValue = 0; // success 
		
		String[][] anArrayOfStringsCC;
		Double[][] anArrayOfCoords;
		
		String s = null;
		
		// Can only match in the last 1000 records in this batch
		int arrarySize=1000;
		
		anArrayOfStringsCC = new String[arrarySize][2];
		anArrayOfCoords = new Double[arrarySize][3];
		
		int i=0;
		
		Process p = Runtime.getRuntime().exec("python readrandom0.5.py -i 10  -f 2");
		
		
		BufferedReader stdInput = new BufferedReader(new
					InputStreamReader(p.getInputStream()));

           BufferedReader stdError = new BufferedReader(new
        		   	InputStreamReader(p.getErrorStream()));

           // read the output from the command
          // System.out.println("Here is the standard output of the command:\n");
           while ((s = stdInput.readLine()) != null) {
              // System.out.println(s);
               JSONObject obj = new JSONObject(s);
				
				int lat_float = obj.getJSONObject("geometry").getString("coordinates").indexOf(squarebrq);
				int long_float = obj.getJSONObject("geometry").getString("coordinates").indexOf(comma);
				int long_float_end = obj.getJSONObject("geometry").getString("coordinates").indexOf(squarebrqend);
			
				anArrayOfStringsCC[i][0]=obj.getJSONObject("properties").getString("cc");
				
				int linelength = s.indexOf("}}");
				anArrayOfStringsCC[i][1]=s.substring(0, linelength);
				anArrayOfCoords[i][0]=Double.valueOf(obj.getJSONObject("geometry").getString("coordinates").substring(lat_float + 1, long_float));
				anArrayOfCoords[i][1]=Double.valueOf(obj.getJSONObject("geometry").getString("coordinates").substring(long_float + 1, long_float_end ));
				
				i++;
           }
           for (int j=0; j<=i; j++)
				for (int k=j+1; k<=i; k++)
				{
					{
						if (anArrayOfStringsCC[j][0].equals(anArrayOfStringsCC[k][0])) {
							calcGeoDistance calc1 = new calcGeoDistance();
							Double distance1 = calc1.distance(anArrayOfCoords[j][0], anArrayOfCoords[j][1], anArrayOfCoords[k][0], anArrayOfCoords[k][1], "M");
							anArrayOfCoords[k][2]=distance1;
						} 
					}
				}
			
			for (int j=0; j<=i; j++) {
				if (anArrayOfCoords[j][2] != null) {
					System.out.println(anArrayOfStringsCC[j][1] + ", \"geodistance\": "+ anArrayOfCoords[j][2] + "}}");
				} else {
					System.out.println(anArrayOfStringsCC[j][1] + ", \"geodistance\": "+ "0" + "}}");
				}
			}
            
           // read any errors from the attempted command
           // System.out.println("Here is the standard error of the command (if any):\n");
           while ((s = stdError.readLine()) != null) {
        	   retunValue=1;
               System.out.println(s);
           }
		
		return retunValue;
	}
	
	
	public static int parseJsonFromFile() throws IOException, JSONException {
	//	String jsonData = "";
		String comma = ",";
		String squarebrq = "[";
		String squarebrqend = "]";
		BufferedReader br = null;
		
		String[][] anArrayOfStringsCC;
		Double[][] anArrayOfCoords;
		
		int arrarySize=100;
		
		anArrayOfStringsCC = new String[arrarySize][2];
		anArrayOfCoords = new Double[arrarySize][3];
		
		int i=0;
		
		try {
			String line;
			br = new BufferedReader(new FileReader("/Users/eorgad/tmp2.json"));
			
			while ((line = br.readLine()) != null) {
				
				//		jsonData += line + "\n";
				JSONObject obj = new JSONObject(line);
				
				int lat_float = obj.getJSONObject("geometry").getString("coordinates").indexOf(squarebrq);
				int long_float = obj.getJSONObject("geometry").getString("coordinates").indexOf(comma);
				int long_float_end = obj.getJSONObject("geometry").getString("coordinates").indexOf(squarebrqend);
			
				anArrayOfStringsCC[i][0]=obj.getJSONObject("properties").getString("cc");
				
				int linelength = line.indexOf("}}");
				anArrayOfStringsCC[i][1]=line.substring(0, linelength);
				anArrayOfCoords[i][0]=Double.valueOf(obj.getJSONObject("geometry").getString("coordinates").substring(lat_float + 1, long_float));
				anArrayOfCoords[i][1]=Double.valueOf(obj.getJSONObject("geometry").getString("coordinates").substring(long_float + 1, long_float_end ));
				i++;
			}
			
			for (int j=0; j<=i; j++)
				for (int k=j+1; k<=i; k++)
				{
					{
						if (anArrayOfStringsCC[j][0].equals(anArrayOfStringsCC[k][0])) {
							calcGeoDistance calc1 = new calcGeoDistance();
							Double distance1 = calc1.distance(anArrayOfCoords[j][0], anArrayOfCoords[j][1], anArrayOfCoords[k][0], anArrayOfCoords[k][1], "M");
							anArrayOfCoords[k][2]=distance1;
						} 
					}
				}
			
			for (int j=0; j<=i; j++) {
				if (anArrayOfCoords[j][2] != null) {
					System.out.println(anArrayOfStringsCC[j][1] + ", \"geodistance\": "+ anArrayOfCoords[j][2] + "}}");
				} else {
					System.out.println(anArrayOfStringsCC[j][1] + ", \"geodistance\": "+ "0" + "}}");
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return 0;
		
	}
	
	public static void main(String[] args) throws JSONException, IOException {
		
		// just execute the Python script and parse to match records
		// if we find a match we calculate the distance and enrich the results
		executePython();
	
	}
	
	
}