package pl.com.pl.sutip.wrapper;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class UploadFile
{
	private String schedule;
	private String link;
	private CommonsMultipartFile fileData;
	 
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

	public CommonsMultipartFile getFileData()
	{
	    return fileData;
	}
	 
	public void setFileData(CommonsMultipartFile fileData)
	{
	    this.fileData = fileData;
	}
}
