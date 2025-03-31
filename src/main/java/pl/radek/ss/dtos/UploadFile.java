package pl.radek.ss.dtos;

import org.springframework.web.multipart.MultipartFile;

public class UploadFile
{
	private String schedule;
	private String link;
	private MultipartFile fileData;
    
    public UploadFile() { }
    
    public UploadFile(MultipartFile fileData) {
        this.fileData = fileData;
    }
    
    public String getSchedule()
	{
		return schedule;
	}

	public void setSchedule(String schedule)
	{
		this.schedule = schedule;
	}

	public String getLink()
	{
		return link;
	}

	public void setLink(String link)
	{
		this.link = link;
	}

	public MultipartFile getFileData()
	{
	    return fileData;
	}
	 
	public void setFileData(MultipartFile fileData)
	{
	    this.fileData = fileData;
	}
}
