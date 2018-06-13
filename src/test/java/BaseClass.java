import java.awt.BorderLayout;
import java.awt.Container;
import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

import org.openqa.selenium.WebDriver;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import com.relevantcodes.extentreports.ExtentTest;



public class BaseClass {
	
	Calendar getCalDate = Calendar.getInstance();
		
	protected WebDriver driver;
	//protected InputText input_text =  new InputText();
	////Clicks click =  new Clicks();
	//SelectDropdown selectdropdown = new SelectDropdown();
	//protected Validations validation= new Validations();
	protected KeyPress keypress = new KeyPress();
	protected ExtentTest logger;	//Main class to generate the Logs and add to the report
	protected String executionreportpath, errormessage, testcasepath, browsername, sheetName, preferencesSheetName;
	protected int sheetnumber, invocationcount;	//invcationcount to run multiple times for same set of test data
	
	protected Object preferencesdata[][]=new Object[4][1];
	protected Boolean exceptionerror;
	protected String stepdescription, objectName, stepid;
	protected JFrame f = new JFrame("Starting Test Execution...");
	protected Boolean multipleExecutionsDifferentTestData=false;
	
@BeforeClass(alwaysRun=true)
public void setUp() throws Exception 
{		
	checkLicense();  // Set the date before distributing in this method.
	testcasepath = InvokeMaster.sheetDirPathAndName; 
	sheetName = "TestCases";
	preferencesSheetName = "Control"; //Mention it here also	
	
	progressBar(); // Call the Progress Bar code
	f.setVisible(true); //Display the Progress Bar
	LogManager.getLogManager().reset();
	Logger globalLogger = Logger.getLogger(java.util.logging.Logger.GLOBAL_LOGGER_NAME);
	globalLogger.setLevel(java.util.logging.Level.OFF);
	
	//ExcelDataConfig excelreadpreferences = new ExcelDataConfig(System.getProperty("user.dir")+"/Preferences.xlsm",preferencesSheetName);	
	ExcelDataConfig excelreadpreferences = new ExcelDataConfig(testcasepath,preferencesSheetName);
	//preferencesdata = new Object[5][1];
	for(int i=0;i<4;i++)   //Initializing Array to rows-1. First row is just headings and make sure every column cell has a text
	{
		for(int j=0;j<1;j++)  //Columns value is one more than the index so less than sign
		{
			preferencesdata[i][j]=excelreadpreferences.getData(preferencesSheetName, i+1, j+1);  //Picking data from the 2nd row in excel sheet, so i+1 and also 2nd column
		}					
	}
//	if (((String) preferencesdata[0][0]).trim().length() > 0) //check there is at least one character
//	{
			//testcasepath = InvokeMaster.sheetDirPath;   //(String) preferencesdata[0][0];
			//sheetName = "TestCases";  //FiXING it, so below block code not required
		/**	if (((String) preferencesdata[1][0]).matches("[0-9]+") && ((String) preferencesdata[1][0]).trim().length() > 0)	//check for integer values only
			{
				sheetnumber =  Integer.parseInt((String) preferencesdata[1][0]);
			}
			else	sheetnumber = 0; 	// default is 0			
			**/
				
			//check null chrome browser parameter
			if (!preferencesdata[0][0].equals("0") && ((String) preferencesdata[0][0]).trim().length() > 0)  	browsername = (String) preferencesdata[0][0];  
			else								browsername = "Chrome";						//Default browser is Chrome, if none specified
			//check null report path parameter
			if (!preferencesdata[1][0].equals("0") && ((String) preferencesdata[1][0]).trim().length() > 0)		
			{
				executionreportpath = (String) preferencesdata[1][0];
				if (!(executionreportpath.substring(executionreportpath.length()-1).equals("\\")) && executionreportpath.length()>2)  //To cover the case where user doesn't provide '\' in front of the path. In that case screenshots stay in the parent directory 
				{ 
						 executionreportpath = executionreportpath+"\\";  
				}				
			}
			else		executionreportpath = InvokeMaster.sheetDirPath;					//Report path local sheet directory
			// Check Multiple executions with same data
		
			executionreportpath = createReportDirectory(executionreportpath);
			
			
				
			if (!preferencesdata[3][0].equals("0") && ((String) preferencesdata[3][0]).matches("[0-9]+") && ((String) preferencesdata[3][0]).trim().length() > 0 )	
			{	
				multipleExecutionsDifferentTestData = true;
			}
			ReportScreenshotUtility.GetExtent(executionreportpath);
			
			//Setup Logging off - First one seems to be working
			
			
			/**Logger globalLogger = Logger.getLogger("global");
			/**Handler[] handlers = globalLogger.getHandlers();
			for(Handler handler : handlers) {
			 globalLogger.removeHandler(handler);}
			**/
			//Logger.getLogger("");
			
		///	propertyconfig = new ConfigReader(); //Read the Config Property value
			//System.setProperty("webdriver.gecko.driver", propertyconfig.getGeckoPath());  //gecko is required for Selenium 3
		///	System.setProperty("webdriver.chrome.driver", propertyconfig.getChromePath());
			///String app_url = JOptionPane.showInputDialog(null,"Enter Application URL"); //To create window
		///	System.setProperty("webdriver.chrome.driver", "C:\\chromedriver.exe");
		///	driver = new ChromeDriver();
			///driver.get("https://"+app_url); // To open url in browse
			//report = new ExtentReports(System.getProperty("user.dir")+ propertyconfig.getReportPath()); //Set the HTML Execution Report Path. Putting another parameter TRUE will overwrite the file everytime.
	
			//ReportScreenshotUtility.report.loadConfig(new File(System.getProperty("user.dir")+"/src/main/resources/extent-config.xml")); //Load the config settings frot he report from xml.
			
	
//	}
//	else
//	{
		//System.out.println("Invalid or unspecified test case path");
//		JOptionPane.showMessageDialog(null, "Test Case path not specified in the Preferences sheet.");
//		System.exit(1);
		
//	}
}
	@AfterClass(alwaysRun=true)
	public void tearDown() throws Exception 
	 {
		StringBuffer verificationErrors = new StringBuffer();
		
	    if ((driver != null))		driver.quit();  // For DEBUGGING, Disable it otherwise ENABLE  
	    String verificationErrorString = verificationErrors.toString();
	    if (!"".equals(verificationErrorString)) 
	    {
	      AssertJUnit.fail(verificationErrorString);
	    }
	    ReportScreenshotUtility.report.flush();	    
	    ReportScreenshotUtility.report.endTest(logger);
	    ReportScreenshotUtility.report.close();
	    //ReportScreenshotUtility.report.close(); It's causing error -  Close was called before test could end safely using EndTest.
	    
	    //super.getReport().endTest(super.getLogger());
	    //super.getReport().flush();
	  }

	public void progressBar()
	{	
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container content = f.getContentPane();
		JProgressBar progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		//progressBar.setValue(25);
		//progressBar.setStringPainted(true);
		//Border border = BorderFactory.createTitledBorder("Launching...");
		///progressBar.setBorder(border);
		content.add(progressBar, BorderLayout.NORTH);
		f.setSize(600, 100);
		//f.setAlwaysOnTop(true);
		f.setLocationRelativeTo(null);
	}
	
	private void checkLicense()
	{
		//PublicServer GetDateTime
		
		Date serverDate = (PublicServerTime.getNTPDate());
		if(serverDate == null) 
		{
			JFrame frame = new JFrame("JOptionPane showMessageDialog example");
		    JOptionPane.showMessageDialog(frame,
		        "Problem with license authentication. Please contact the vendor.");
		  	System.exit(0);
		}
				
				
		@SuppressWarnings("deprecation")
		int dateMon = serverDate.getMonth()+1;
		@SuppressWarnings("deprecation")
		int dateYear =  serverDate.getYear()+1900;   //getYear gives currentyear-1900
		//System.out.println(dateMon + " " + dateYear);
		
		
	//Local Machine GetDateTime	
		//System.out.println("Date: "+ getCalDate.get(Calendar.MONTH)+"/"+getCalDate.get(Calendar.YEAR));
		// System.out.println(System.getProperty("user.name")); To check the USERNAME of the machine
	
		//	int dateMon = getCalDate.get(Calendar.MONTH)+1;
		//	int dateYear = getCalDate.get(Calendar.YEAR);
		
		//Common code to validate
		if (dateMon >= 7 && dateYear >= 2019)	// Feb 2019
			{
				System.out.println("License Expired");
				JFrame frame = new JFrame("JOptionPane showMessageDialog example");
			    JOptionPane.showMessageDialog(frame,
			        "License Expired. Please contact the vendor.");
			  	System.exit(0);
			}
	}
	
	public String createReportDirectory(String path)
	{
		String date, month, hour, min, sec;
		date = Integer.toString(getCalDate.get(Calendar.DAY_OF_MONTH));
		month = Integer.toString((getCalDate.get(Calendar.MONTH))+1);
		hour = Integer.toString(getCalDate.get(Calendar.HOUR_OF_DAY));
		min = Integer.toString(getCalDate.get(Calendar.MINUTE));
		sec = Integer.toString(getCalDate.get(Calendar.SECOND));
		
		if(date.length()==1) date = "0"+date;
		if(month.length()==1) month = "0"+month;		
		if(hour.length()==1) hour = "0"+hour;
		if(min.length()==1) min = "0"+min;
		if(sec.length()==1) sec = "0"+sec;
		
		path = executionreportpath+"\\"+date+month+getCalDate.get(Calendar.YEAR)+"_"+hour+min+sec+"\\";
		
		if(new File(path).mkdirs())	 return path;		// Create directory/folder. If successful then path is that folder
		else return executionreportpath;
	}
	
	
	
}
