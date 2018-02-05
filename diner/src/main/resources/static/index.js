class IndexView {
    constructor() {
        this.eatContainer = document.querySelector("#Eat");
        this.drinksContainer = document.querySelector("#Drinks");

    }

    goHome() {


        fetch("/api/menu").then(res => res.json()).then(menu => {
            menu.dishes.forEach((d) => {
                this.eatContainer.appendChild(this.newH5(d.name));
                this.eatContainer.appendChild(this.newDescription(d));
                this.eatContainer.appendChild(document.createElement("br"));
            });
            menu.drinks.forEach((d) => {
                this.drinksContainer.appendChild(this.newH5(d.name));
                this.drinksContainer.appendChild(this.newDescription(d));
                this.drinksContainer.appendChild(document.createElement("br"));
                
            });
        }
        );

    }

    newH5(text) {
        var h = document.createElement("h5");
        h.appendChild(document.createTextNode(text));
        return h;
    }

    newDescription(d) {
        var p = document.createElement("p");
        p.className = "w3-text-grey";
        var price = "" + d.price;
        p.appendChild(document.createTextNode(d.description + " " + price.slice(0, price.length-2) + "." + price.slice(price.length-2, price.length)));
        return p;
    }

}

const view = new IndexView();
view.goHome();



