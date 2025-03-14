  var map;
  var marker;
  function initializeManage()
  {
        var latitude = document.getElementById('lat').value;
		var longitude = document.getElementById('lon').value;
		var eventName = document.getElementById('name').value;
		var gmOptions =
		{
		  center: new google.maps.LatLng(latitude, longitude),
		  zoom: 8,
		  mapTypeId: google.maps.MapTypeId.ROADMAP
		};
		
		map = new google.maps.Map(document.getElementById("map_canvas"), gmOptions);
		marker = new google.maps.Marker({
		    position: new google.maps.LatLng(latitude, longitude),
		    map: map,
		    title: eventName
		});

		// Ustawia marker na środku po 1 sekundzie od zmian 
		google.maps.event.addListener(map, 'center_changed', function() {
		window.setTimeout(function() {
    			marker.setPosition(new google.maps.LatLng(map.getCenter().lat(), map.getCenter().lng()));
    		}, 1000);
        });
  }
  
  function initializeCreate()
  {
        var latitude = document.getElementById('lat').value;
        var longitude = document.getElementById('lon').value;
        var gmOptions =
        {
          center: new google.maps.LatLng(latitude, longitude),
          zoom: 8,
          mapTypeId: google.maps.MapTypeId.ROADMAP
        };
        
        map = new google.maps.Map(document.getElementById("map_canvas"), gmOptions);
        marker = new google.maps.Marker({
            position: new google.maps.LatLng(latitude, longitude),
            map: map,
            title: "eventMarker"
        });
        
        // Ustawia marker na środku po 1 sekundzie od zmian 
        google.maps.event.addListener(map, 'center_changed', function() {
    		window.setTimeout(function() {
    			marker.setPosition(new google.maps.LatLng(map.getCenter().lat(), map.getCenter().lng()));
    		}, 1000);
        });
  }
  
  function initializeDetails()
  {
        var latitude = document.getElementById('lat').value;
        var longitude = document.getElementById('lon').value;
        var eventName = document.getElementById('name').value;
        var gmOptions =
        {
          center: new google.maps.LatLng(latitude, longitude),
          zoom: 8,
          mapTypeId: google.maps.MapTypeId.ROADMAP
        };
        map = new google.maps.Map(document.getElementById("map_canvas"), gmOptions);
        
        marker = new google.maps.Marker({
            position: new google.maps.LatLng(latitude, longitude),
            map: map,
            title: eventName
        });
  }
  
  function eventSubmit()
  {
	  document.getElementById('lat').value = map.getCenter().lat();
	  document.getElementById('lon').value = map.getCenter().lng();
  }