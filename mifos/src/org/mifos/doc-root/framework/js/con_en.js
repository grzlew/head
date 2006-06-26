/********************************************************************************
* This file contains the javascript functions for the ENGLISH language
* author - Rajender saini
* version - 1
/

/*********************************************************************************
* function for doing the validation in english 
*  function name : validateEnglish
*  arg1(txt) -  string you want to validate
*  arg2(min) - Min value it can take 
*  arg3(max) - Max value it can take 
*  usage - for validation of valuse entered in english
************************************************************************************/

function envalidate(txt,min,max)
{
	locale = document.all.h_user_locale.value;	
	
	if(min==null)
		min=1;
	
	
	if(max==null)
	{
				
		if(true == pattern.test(txt.toString()))
		{
			if(txt<min)
			    eval(locale+ "AlertMinNumber(min)");
			max=txt;
		}
		else
		{
		
			var format="";
		    for ( i = 0 ; i < whole_part_length ; i++)
		    {
		     format += "X"
		    }
		    if (fraction_part_length > 1)
		    {
			    format += ".";
			    for ( i = 0 ; i < fraction_part_length ; i++)
			    {
			     format += "X"
		    	}
		    }

			eval(locale+ "Alertformat(format)");
				return false;
				
		}

		
		
	}
	
	
	
		
	else if( true == pattern.test(txt.toString()))

	{ 
		

		//passed pattern test  check for min max
		if(txt >= min && txt <= max)
		{
			//passed all test
		}
		else
		{
			  //    alert("element should be in range "+min+" - to - " +max );
			  eval(locale + "AlertRange(min,max)");
				  return false;	
		}

	}
	else
	{
	   var format="";
		for ( i = 0 ; i < whole_part_length ; i++)
		{
		 format += "X"
		}
		if (fraction_part_length > 1)
		{
			format += ".";
			for ( i = 0 ; i < fraction_part_length ; i++)
			{
			 format += "X"
			}
		}
		
			eval(locale+ "Alertformat(format)");
			return false;
		
	  


	}

	
}
/*************************************************************************************
* function enOnkeyPress
* arg1 (e) - event object
* usage - This function is called when key is pressed while focus is in inputbox and locale is english
***************************************************************************************/

function enOnkeyPress(element,e,fmt )
{
	
    return genericOnkeyPress(fmt,element,e,ENGLISH.CODE_0,ENGLISH.CODE_1,ENGLISH.CODE_DECIMAL);
}

function enAlertRange(min,max)
{
   alert(" Value entered should be in range : " + min + " - " + max);
}
function enAlertformat(format)
{
	alert( "Please enter a valid format  " + format); 
}
function enAlertMinNumber(min)
{
	alert( "Please enter atleast " +min+ " numbers" ); 
}