<script>
try{
	var xhr;
	var t=new Date().getTime();
	var status="start";
	var timing=new Array(3);

	window.onunload=function(){
		timing[2]="r:"+(new Date().getTime()-t);
		document.createElement("img").src="/_Incapsula_Resource?ES2LURCT=67&t=78&d="+encodeURIComponent(status+" ("+timing.join()+")")
	};

	if(window.XMLHttpRequest){
		xhr=new XMLHttpRequest;

	}else{
		xhr=new ActiveXObject("Microsoft.XMLHTTP")
	}

	xhr.onreadystatechange=function(){
		switch(xhr.readyState){
			case 0:
				status=new Date().getTime()-t+": request not initialized ";
			break;
			case 1:
				status=new Date().getTime()-t+": server connection established";
			break;
			case 2:
				status=new Date().getTime()-t+": request received";
			break;
			case 3:
				status=new Date().getTime()-t+": processing request";
			break;
			case 4:
				status="complete";
				timing[1]="c:"+(new Date().getTime()-t);
				if(xhr.status==200){
					parent.location.reload()
				}
			break;
		}
	};

	timing[0]="s:"+(new Date().getTime()-t);
	xhr.open("GET","/_Incapsula_Resource?SWHANEDL=8314766217132623525,15974946931059580958,7337869607244085408,451525",false);
	xhr.send(null);

}catch(c){
	status+=new Date().getTime()-t+" incap_exc: "+c;
	document.createElement("img").src="/_Incapsula_Resource?ES2LURCT=67&t=78&d="+encodeURIComponent(status+" ("+timing.join()+")")
};
</script>