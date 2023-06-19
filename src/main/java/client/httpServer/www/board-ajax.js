function refreshView() {
    var notes = [];
    var board = [];

    let request = new XMLHttpRequest();


    request.onload = function() {
        notes = JSON.parse(this.responseText);
        generateTable(board, notes);
        setTimeout(refreshView, 2000);
    };

    request.ontimeout = function() {
        var header = document.createElement("h1");
        var headerText = document.createTextNode("Server timeout, still trying...");
        header.appendChild(headerText);
        document.body.appendChild(header);
        setTimeout(refreshView, 100);
    };

    request.onerror = function() {
        var header = document.createElement("h1");
        var headerText = document.createTextNode("No server reply, still trying...");
        header.appendChild(headerText);
        document.body.appendChild(header);
        setTimeout(refreshView, 5000);
    };

    request.open("GET", "http://localhost:8000/notes", true);
    request.timeout = 5000;
    request.send();
}
function generateTable(board, notes) {
    // creates a <table> element and a <tbody> element
    const tbl = document.createElement("table");
    const tblBody = document.createElement("tbody");
    let count = 0;

    // creating all cells
    for (let i = 1; i <= 5; i++) {
        // creates a table row
        const row = document.createElement("tr");

        for (let j = 1; j <= 5; j++) {
            //count++;
            // Create a <td> element and a text node, make the text
            // node the contents of the <td>, and put the <td> at
            // the end of the table row
            const cell = document.createElement("td");
            let cellText = document.createTextNode("");
            //let cellImg = document.createElement("img");
            /*if (count <= notes.length) {
                cellText = document.createTextNode(notes[count - 1].postit.text);
                //cellImg.src = notes[count - 1].image;
                cell.appendChild(cellText);
                //cell.appendChild(cellImg);
            }*/
            notes.map(note => {
                if(parseInt(note.postit.row) == i && parseInt(note.postit.column) == j){
                    cellText = document.createTextNode(note.postit.text);
                    cell.appendChild(cellText);
                }

            })
            row.appendChild(cell);
        }

        // add the row to the end of the table body
        tblBody.appendChild(row);
    }

    // put the <tbody> in the <table>
    tbl.appendChild(tblBody);

    // appends <table> into <body>
    var header = document.createElement("h1");
    var headerText = document.createTextNode("Random title");
    header.appendChild(headerText);
    document.body.appendChild(header);
    document.body.appendChild(tbl);

    // sets the border attribute of tbl to '2'
}
