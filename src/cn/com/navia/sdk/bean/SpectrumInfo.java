package cn.com.navia.sdk.bean;

import java.io.File;

public class SpectrumInfo {
	private RetVal_UpdateItem updateItem;
	private String file;
	private File specFile;

    public RetVal_UpdateItem getUpdateItem() {
        return updateItem;
    }

    public SpectrumInfo(RetVal_UpdateItem updateItem ) {
		super();
		this.updateItem = updateItem;

	}

	public String getFile() {
		return file;
	}
	
	public File getSpecFile(File specDir){
		if(specFile == null){
			this.specFile = new File(specDir, file);
		}
		return this.specFile;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public File getSpecFile() {
		return specFile;
	}

	public void setSpecFile(File specFile) {
		this.specFile = specFile;
	}
}