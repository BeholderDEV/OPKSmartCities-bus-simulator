const pollingInterval = 3000

const simulation = {
	
    start: function (callback) {

        $.ajax({ type: "POST", url: '/simulation/start' }).done(function (result) {
            
            callback? callback(result.state) : ''

        }).fail(function (error) {
        	alert('ERROR: ' + error.statusText)
        })
    },

    stop: function (callback) {

        $.ajax({ type: "POST", url: '/simulation/stop' }).done(function (result) {
            
        	callback? callback(result.state) : ''

        }).fail(function (error) {
        	alert('ERROR: ' + error.statusText)
        })
    },
    
    getState: function(callback) {
    	
    	 $.ajax({ type: "GET", url: '/simulation/state' }).done(function (data) {
             callback(data.state)

         }).fail(function (error) {
        	 alert('ERROR: ' + error.statusText)
         })
	}
}

function readState(){
	
	console.log('Polling state...')
	
	simulation.getState(function(state){
		
		$('#button-simulate').removeAttr('disabled')
		
		if (state.status === 'RUNNING'){
			
			$('#button-simulate').html('Stop simulation').unbind('click').click(function () {
			
				$('#button-simulate').attr('disabled', true)
		    	simulation.stop()
		    })			
		}
		else if (state.status === 'STOPPED'){
			
			$('#button-simulate').html('Start simulation').unbind('click').click(function () {
				
				$('#button-simulate').attr('disabled', true)
		    	simulation.start()
		    })
		}
		
		setTimeout(readState, pollingInterval);
	})
}

$(document).ready(readState)

