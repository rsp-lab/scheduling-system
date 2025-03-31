	var professorId = 0;
	var courseId = 0;
	var studentsId = 0;
	
	function addPromoter()
	{
		var elementPId = document.createElement("input");
		var elementPName = document.createElement("input");
		
		elementPId.setAttribute("id", "short");
		elementPId.setAttribute("type", "text");
		elementPId.setAttribute("value", ++professorId);
		elementPId.setAttribute("name", "prof");
		elementPId.setAttribute("disabled", "disabled");
		elementPName.setAttribute("id", "long");
		elementPName.setAttribute("type", "text");
		elementPName.setAttribute("name", "prof");
		
        var professors = document.getElementById("promoters");
        // professors.innerHTML += "PROFESSOR ";
        professors.innerHTML += "Id: ";
        professors.appendChild(elementPId);
        professors.innerHTML += " ";
        professors.innerHTML += "Tytuł, Imię, Nazwisko: ";
        professors.appendChild(elementPName);
        professors.innerHTML += "</br>";
	}

	function addCourse()
	{
		var elementCId = document.createElement("input");
		var elementCName = document.createElement("input");
		
		elementCId.setAttribute("id", "short");
		elementCId.setAttribute("type", "text");
		elementCId.setAttribute("value", ++courseId);
		elementCId.setAttribute("disabled", "disabled");
		elementCId.setAttribute("name", "cour");
		elementCName.setAttribute("id", "long");
		elementCName.setAttribute("type", "text");
		elementCName.setAttribute("name", "cour");
		
        var courses = document.getElementById("courses");
        // courses.innerHTML += "PRZEDMIOT ";
        courses.innerHTML += "Id: ";
        courses.appendChild(elementCId);
        courses.innerHTML += " ";
        courses.innerHTML += "Przedmiot spotkania: ";
        courses.appendChild(elementCName);
        courses.innerHTML += "</br>";
	}
	
	function addRoom()
	{
		var elementRName = document.createElement("input");
		var elementRNrSeats = document.createElement("input");
		var elementRLab = document.createElement("select");
		var elementROptionTrue = document.createElement('option');
		var elementROptionFalse = document.createElement('option');
		
		elementRName.setAttribute("id", "mid");
		elementRName.setAttribute("type", "text");
		elementRName.setAttribute("name", "room");
		elementRNrSeats.setAttribute("id", "short");
		elementRNrSeats.setAttribute("type", "text");
		elementRNrSeats.setAttribute("name", "room");
		elementROptionTrue.value = 'true';
		elementROptionTrue.appendChild(document.createTextNode('tak'));
		elementROptionFalse.value = 'false';
		elementROptionFalse.appendChild(document.createTextNode('nie'));
		elementRLab.appendChild(elementROptionTrue);
		elementRLab.appendChild(elementROptionFalse);
		elementRLab.setAttribute("name", "room");
		
        var rooms = document.getElementById("rooms");
        // rooms.innerHTML += "POKÓJ ";
        rooms.innerHTML += "Nazwa: ";
        rooms.appendChild(elementRName);
        rooms.innerHTML += " ";
        rooms.innerHTML += "Laboratorium: ";
        rooms.appendChild(elementRLab);
        rooms.innerHTML += " ";
        rooms.innerHTML += "Ilość miejsc: ";
        rooms.appendChild(elementRNrSeats);
        rooms.innerHTML += "</br>";
	}
	
	function addGroup()
	{
		var elementGId = document.createElement("input");
		var elementGName = document.createElement("input");
		var elementGNrStudents = document.createElement("input");
		
		elementGId.setAttribute("id", "short");
		elementGId.setAttribute("type", "text");
		elementGId.setAttribute("value", ++studentsId);
		elementGId.setAttribute("disabled", "disabled");
		elementGId.setAttribute("name", "group");
		elementGName.setAttribute("id", "long");
		elementGName.setAttribute("type", "text");
		elementGName.setAttribute("name", "group");
		elementGNrStudents.setAttribute("id", "short");
		elementGNrStudents.setAttribute("type", "text");
		elementGNrStudents.setAttribute("name", "group");
		
        var courses = document.getElementById("groups");
        // courses.innerHTML += "GRUPA ";
        courses.innerHTML += "Id: ";
        courses.appendChild(elementGId);
        courses.innerHTML += " ";
        courses.innerHTML += "Nazwa grupy: ";
        courses.appendChild(elementGName);
        courses.innerHTML += " ";
        courses.innerHTML += "Ilość uczestników: ";
        courses.appendChild(elementGNrStudents);
        courses.innerHTML += "</br>";
	}
	
	function addEvent()
	{
		var elemProfessor = document.createElement("input");
		var elemCourse = document.createElement("input");
		var elemGroups = document.createElement("input");
		var elemLab = document.createElement("select");
		var elemDuration = document.createElement("input");
		var elemOptionTrue = document.createElement('option');
		var elemOptionFalse = document.createElement('option');
		
		elemProfessor.setAttribute("id", "short");
		elemProfessor.setAttribute("type", "text");
		elemProfessor.setAttribute("name", "cc");
		elemCourse.setAttribute("id", "short");
		elemCourse.setAttribute("type", "text");
		elemCourse.setAttribute("name", "cc");
		elemGroups.setAttribute("id", "mid");
		elemGroups.setAttribute("type", "text");
		elemGroups.setAttribute("name", "cc");
		elemLab.setAttribute("type", "text");
		elemLab.setAttribute("name", "cc");
		elemDuration.setAttribute("id", "short");
		elemDuration.setAttribute("type", "text");
		elemDuration.setAttribute("name", "cc");
		
		elemOptionTrue.value = 'true';
		elemOptionTrue.appendChild(document.createTextNode('tak'));
		elemOptionFalse.value = 'false';
		elemOptionFalse.appendChild(document.createTextNode('nie'));
		elemLab.appendChild(elemOptionTrue);
		elemLab.appendChild(elemOptionFalse);
		
		var classes = document.getElementById("events");
		// classes.innerHTML += "KURS ";
		classes.innerHTML += "Organizator: ";
		classes.appendChild(elemProfessor);
		classes.innerHTML += " ";
		classes.innerHTML += "Przedmiot: ";
		classes.appendChild(elemCourse);
		classes.innerHTML += " ";
		classes.innerHTML += "Grupy uczestników: ";
		classes.appendChild(elemGroups);
		classes.innerHTML += " ";
		classes.innerHTML += "Wymagane laboratorium: ";
		classes.appendChild(elemLab);
		classes.innerHTML += " ";
		classes.innerHTML += "Czas trwania w godzinach: ";
		classes.appendChild(elemDuration);
		classes.innerHTML += "</br>";
	}
	
    function append_href(theLink)
    {
        href = theLink.href;
        
        var profParam = "";
        var elementsProfessors = document.getElementsByName("prof");
        for(x=0; x<elementsProfessors.length; x++)
        {
        	profParam += elementsProfessors[x].value;
        	if ( x !== (elementsProfessors.length-1) )  
        	{
        		profParam += "_";
        	}
        }
        
        var courParam = "";
        var elementsCourses = document.getElementsByName("cour");
        for(x=0; x<elementsCourses.length; x++)
        {
        	courParam += elementsCourses[x].value;
        	if ( x !== (elementsCourses.length-1) )  
        	{
        		courParam += "_";
        	}
        }
        
        var roomParam = "";
        var elementsRooms = document.getElementsByName("room");
        for(x=0; x<elementsRooms.length; x++)
        {
        	roomParam += elementsRooms[x].value;
        	if ( x !== (elementsRooms.length-1) )  
        	{
        		roomParam += "_";
        	}
        }
        
        var groupParam = "";
        var elementsGroups = document.getElementsByName("group");
        for(x=0; x<elementsGroups.length; x++)
        {
        	groupParam += elementsGroups[x].value;
        	if ( x !== (elementsGroups.length-1) )  
        	{
        		groupParam += "_";
        	}
        }
        
        var ccParam = "";
        var elementsClasses = document.getElementsByName("cc");
        for(x=0; x<elementsClasses.length; x++)
        {
        	ccParam += elementsClasses[x].value;
        	if ( x !== (elementsClasses.length-1) )  
        	{
        		ccParam += "_";
        	}
        }
        
        var sresults = '?promoters=' + profParam + '&courses=' + courParam + '&rooms=' + roomParam + '&groups=' + groupParam + '&events=' + ccParam;
        theLink.href = href + sresults;
    }