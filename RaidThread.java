import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

public class RaidThread extends Thread 
{
	String primaryPath;
	String raidPath;
	
	public RaidThread(String primaryPath, String raidPath){
		this.primaryPath = primaryPath;
		this.raidPath = raidPath;
	}

	volatile boolean finished = false;

	public void stopMe() {
		finished = true;
	}

	@Override
	public void run() {
		while (!finished) {
			System.out.println("thread started");
			HashMap<String,String> pHash = new HashMap<String,String>();
			HashMap<String,String> rHash = new HashMap<String,String>();
			
			//get files from primary hard disk
			String file = null;
			File folder = new File(primaryPath);
			File[] listOfFiles = folder.listFiles(); 
			for (int i = 0; i < listOfFiles.length; i++) {
				if (listOfFiles[i].isFile()) {
					file = listOfFiles[i].getName();
					pHash.put(file, getHash(listOfFiles[i].toString()));
				}
			}
			
			//get files from this raid thread
			file = "";
			folder = new File(raidPath);
			listOfFiles = folder.listFiles(); 
			for (int i = 0; i < listOfFiles.length; i++) {
				if (listOfFiles[i].isFile()) {
					file = listOfFiles[i].getName();
					rHash.put(file, getHash(listOfFiles[i].toString()));
				}
			}			
			
			
			
			if(rHash.size() > pHash.size()){	// raid is bigger
				for (Map.Entry<String, String> entry: rHash.entrySet()){ 
					if (pHash.containsKey(entry.getKey())){ // if primary hash contains the file in raid hash
						if(!entry.getValue().equals(pHash.get(entry.getKey()))){ //md5 of raid doesn't match primary file
							String newPath = raidPath;
							newPath = newPath + "\\" + rHash.get(entry.getKey());
							File ftd = new File(newPath);
							ftd.delete(); //delete raid file
							
							String pPath = primaryPath;
							pPath = pPath + "\\" + entry.getKey();
							File nFile = new File(pPath);
							File nFile2 = new File(raidPath + "\\" + entry.getKey());
							try { //Copy file from primary to raid
								FileUtils.copyFile(nFile, nFile2);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						}
					} else {
						String newPath = raidPath;
						newPath = newPath + "\\" + entry.getKey();
						File ftd = new File(newPath);
						ftd.delete();
						System.out.println("tried to delete: " + newPath);
					}
				}
			}
			
			
			
			
			else{ //primary is bigger
				for (Map.Entry<String, String> entry : pHash.entrySet()) {
					if(rHash.containsKey(entry.getKey())) {	//if raid hash contains the file in primary hash
						if(!entry.getValue().equals(rHash.get(entry.getKey()))) {	//and if md5 doesn't match...
							String newPath = raidPath;							//then delete file in raid and replace w primary
							newPath = newPath + "\\" + rHash.get(entry.getKey());
							
							File ftd = new File(newPath);	//delete file in hash drive
							ftd.delete();
							
							String pPath = primaryPath;
							pPath = pPath + "\\" + entry.getKey();
							File nFile = new File(pPath);
							File nFile2 = new File(raidPath + "\\" + entry.getKey());
							try {
								FileUtils.copyFile(nFile, nFile2);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} 
					} else {	//copy from primary to raid
						String pPath = primaryPath;
						pPath = pPath + "\\" + entry.getKey();
						File nFile = new File(primaryPath + "\\" + entry.getKey());
						File nFile2 = new File(raidPath + "\\" + entry.getKey());
						try {
							FileUtils.copyFile(nFile, nFile2);
							System.out.println(nFile);
							System.out.println(nFile2);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
			
			
			try {
				sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				System.out.println("couldnt stop thread");
			}
	    }
	}
	
	public String getHash(String path){
		MessageDigest md;
	    StringBuffer sb = null;
		try {
			md = MessageDigest.getInstance("SHA1");
		    FileInputStream fis = new FileInputStream(path);
		    byte[] dataBytes = new byte[1024];
		 
		    int nread = 0; 
		 
		    while ((nread = fis.read(dataBytes)) != -1) {
		      md.update(dataBytes, 0, nread);
		    };
		 
		    byte[] mdbytes = md.digest();
		 
		    //convert the byte to hex format
		    sb = new StringBuffer("");
		    for (int i = 0; i < mdbytes.length; i++) {
		    	sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
		    }
		    fis.close();
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Error hashing the file: " + path + "\n");
		} catch (FileNotFoundException e) {
			System.out.println("Error hashing the file: " + path + "\n");			
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Error hashing the file: " + path + "\n");			
			e.printStackTrace();
		}
	    return sb.toString();
	}
}
