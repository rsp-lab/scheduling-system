	function hideIt()
	{
	    if(document.getElementById) { document.getElementById('layer').style.visibility='hidden';}
	    if(document.layers) {document.layers['layer'].visibility='hide';}

        if(document.getElementById) {document.getElementById('map').style.visibility='visible';}
        if(document.layers) {document.layers['map'].visibility='show';}
	}
	
	function showIt() 
	{
	    if(document.getElementById) {document.getElementById('layer').style.visibility='visible';}
	    if(document.layers) {document.layers['layer'].visibility='show';}

        if(document.getElementById) { document.getElementById('map').style.visibility='hidden';}
        if(document.layers) {document.layers['map'].visibility='hide';}
	}

	var count = 0;
	
    function add()
    {
    	count++;
    	
    	// console.log("LOG: " + count);
    	
        // Pobranie aktualnej daty i godziny 
        var currentTimestamp = new Date();
        
        // Ustawienie wartości wiodącymi zerami w razie konieczności 
        var year = currentTimestamp.getFullYear();
        var month = (currentTimestamp.getMonth()+1)
        if (month < 10)
            month = "0" + month;
        var day = currentTimestamp.getDate()
        if (day < 10)
            day = "0" + day;
        var hours = currentTimestamp.getHours();
        if (hours < 10)
            hours = "0" + hours;
        var minutes = currentTimestamp.getMinutes();
        if (minutes < 10)
            minutes = "0" + minutes;
        
        var currentDate = year + "-" + month + "-" + day;
        var currentTime = hours + ":" + minutes;
        
        // Utworzenie elementu daty 
        var elementDate = document.createElement("input");
        elementDate.setAttribute("type", "text");
        elementDate.style.width = "100px";
        elementDate.setAttribute("name", "intervalsText");
        elementDate.setAttribute("value", currentDate);

        // Utworzenie elementu czasu
        var elementTime = document.createElement("input");
        elementTime.setAttribute("type", "text");
        elementTime.setAttribute("name", "intervalsText");
        elementTime.setAttribute("value", currentTime);
        
        // Dodawanie kontrolek z przedziałami 
        var intervalList = document.getElementById("intervalList");
     
        intervalList.innerHTML += "From ➜ ";
        intervalList.innerHTML += "Date: ";
        intervalList.appendChild(elementDate);
        intervalList.innerHTML += " ";
        intervalList.innerHTML += "Time: ";
        intervalList.appendChild(elementTime);
        intervalList.innerHTML += "<br/>";
        intervalList.innerHTML += "Until ➜ ";
        intervalList.innerHTML += "Date: ";
        intervalList.appendChild(elementDate);
        intervalList.innerHTML += " ";
        intervalList.innerHTML += "Time: ";
        intervalList.appendChild(elementTime);
        intervalList.innerHTML += "<br/>";
        
    }
    
    function deleteAll(list)
    {
        var list = document.getElementById(list); 
        if ( list.hasChildNodes() )
        {
            while ( list.childNodes.length >= 1 )
            {
                list.removeChild( list.firstChild );       
            } 
        }
        counterAdd = 0;
    }
    
    function validate()
    {
        var elementy = document.getElementsByName("intervalsText");
        if( elementy !== null)
    	{
            var textParam = "";
            var datePattern = new RegExp("^(\\d{4})\\D?(0[1-9]|1[0-2])\\D?([12]\\d|0[1-9]|3[01])$");
            var timePattern = new RegExp("([01]?[0-9]|2[0-3]):[0-5][0-9]");
            for(x=0; x<elementy.length; x++)
            {
                // Walidacja elementów daty i czasu 
                if( x % 2 == 0)
                {
                    // Weryfikacja daty 
                    if( datePattern.test(elementy[x].value) === true )  
                    {
                        textParam += elementy[x].value;
                    }
                    else
                    {
                        var errorList = document.getElementById("errorList");
                        deleteAll('errorList');
                        textNode = document.createTextNode('Błąd! Wprowadź poprawną date (rrrr-mm-dd) i czas (hh:mm)!');
                        errorList.appendChild(textNode);
                        return false;
                    };
                }
                else
                {
                    // Weryfikacja czasu 
                    if( timePattern.test(elementy[x].value) === true )  
                    {
                        textParam += elementy[x].value;
                    }
                    else
                    {
                        var errorList = document.getElementById("errorList");
                        deleteAll('errorList');
                        textNode = document.createTextNode('Błąd! Wprowadź poprawną date (rrrr-mm-dd) i czas (hh:mm)!');
                        errorList.appendChild(textNode);
                        return false;
                    };                      
                }
                if ( x !== (elementy.length-1) )  
            	{
                	textParam += "#";
            	}
            }
            
            var intervalElement = document.getElementById("intervals");
            intervalElement.value = textParam;
    	}
        return true;
    }