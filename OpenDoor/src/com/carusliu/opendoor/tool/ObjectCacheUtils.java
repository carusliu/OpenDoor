package com.carusliu.opendoor.tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.os.Environment;

import com.carusliu.opendoor.modle.Prize;

public class ObjectCacheUtils {

	static String sdCardPath = Environment.getExternalStorageDirectory().getPath();
	static String cachePath = sdCardPath+"/OpenDoor/cache";
	static String filePath = cachePath+"/superprize.dat";
	public static void saveObjCache(List<Prize> prizeList){
		try {
			File directory = new File(cachePath);  
			directory.mkdirs();
	        File file = new File(filePath);
	        file.createNewFile();  
	        
			FileOutputStream fs = new FileOutputStream(filePath);  
			ObjectOutputStream os =  new ObjectOutputStream(fs);
			for(int i=0;i<prizeList.size();i++){
				os.writeObject(prizeList.get(i));
			}
			os.close();
			fs.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}
	
	public static ArrayList<Prize> readObjCache(){
		ArrayList<Prize> prizeList = new ArrayList<Prize>();
		try {
			FileInputStream fs = new FileInputStream(filePath);  
			ObjectInputStream os =  new ObjectInputStream(fs);
			Prize prize = null;
			while((prize= (Prize)os.readObject())!=null){
				prizeList.add(prize);
			}
			os.close();
			fs.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		return prizeList;
	}
}
