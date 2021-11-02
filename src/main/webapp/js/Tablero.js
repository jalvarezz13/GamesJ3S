class Tablero {
	constructor(ko) {
		let filas = new Array();
		for (let i=0; i<8; i++) {
			filas[i] = new Array();
			for (let j=0; j<8; j++)
				filas[i][j] = new Casilla(ko, null);
		}

		filas[0][0] = new Torre("BLANCO")
		filas[0][7] = new Torre("BLANCO")
		filas[0][1] = new Caballo("BLANCO")
		filas[0][6] = new Caballo("BLANCO")
		filas[0][2] = new Alfil("BLANCO")
		filas[0][5] = new Alfil("BLANCO")
		filas[0][3] = new Dama("BLANCO")
		filas[0][4] = new Rey("BLANCO")

		filas[7][0] = new Torre("NEGRO")
		filas[7][7] = new Torre("NEGRO")
		filas[7][1] = new Caballo("NEGRO")
		filas[7][6] = new Caballo("NEGRO")
		filas[7][2] = new Alfil("NEGRO")
		filas[7][5] = new Alfil("NEGRO")
		filas[7][3] = new Dama("NEGRO")
		filas[7][4] = new Rey("NEGRO")

		for (let i=0; i<8; i++) {
			filas[1][i] = new Peon("BLANCO");
			filas[6][i] = new Peon("NEGRO")
		}

		this.casillas = ko.observableArray(filas); 
	}

	print() {
		let r = "";
		for (let i=0; i<8; i++) {
			for (let j=0; j<8; j++) {
				r = r + this.casillas()[i][j].letra();
			}
			r= r + "\n";
		}
		return r;
	}
}

class Casilla {
	constructor(ko) {
		this.pieza = ko.observable(null);
	}

	letra() {
		if (this.pieza()!=null)
			return this.pieza().letra();
		return " ";
	}
}

class Pieza {
	constructor(color) {
		this.color = color;
	}
}

class Torre extends Pieza {
	constructor(color) {
		super(color);
	}

	letra() {
		if (this.color == "BLANCO")
			return "T";
		else if (this.color == "NEGRO")
			return "t";
	}
}

class Caballo extends Pieza {
	constructor(color) {
		super(color);
	}

	letra() {
		if (this.color == "BLANCO")
			return "C";
		else if (this.color == "NEGRO")
			return "c";
	}
}

class Alfil extends Pieza {
	constructor(color) {
		super(color);
	}

	letra() {
		if (this.color == "BLANCO")
			return "A";
		else if (this.color == "NEGRO")
			return "a";
	}
}

class Dama extends Pieza {
	constructor(color) {
		super(color);
	}

	letra() {
		if (this.color == "BLANCO")
			return "D";
		else if (this.color == "NEGRO")
			return "d";
	}
}

class Rey extends Pieza {
	constructor(color) {
		super(color);
	}

	letra() {
		if (this.color == "BLANCO")
			return "R";
		else if (this.color == "NEGRO")
			return "r";
	}
}

class Peon extends Pieza {
	constructor(color) {
		super(color);
	}

	letra() {
		if (this.color == "BLANCO")
			return "P";
		else if (this.color == "NEGRO")
			return "p";
	}
}