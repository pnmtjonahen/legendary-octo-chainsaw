class IndexView {
    constructor() {
        this.eatContainer = document.querySelector("#Eat");
        this.drinksContainer = document.querySelector("#Drinks");
        this.orderContainer = document.querySelector("#Basket");
        this.drinks = [];
        this.dishes = [];

    }

    goHome() {
        fetch("http://localhost:8080/api/menu").then(res => res.json()).then(menu => {
            menu.dishes.forEach((d) => {
                this.eatContainer.appendChild(this.newH5(d.name));
                this.eatContainer.appendChild(this.newDescription(d));
                this.eatContainer.appendChild(this.newAddToFoodBasket(d));

            });
            menu.drinks.forEach((d) => {
                this.drinksContainer.appendChild(this.newH5(d.name));
                this.drinksContainer.appendChild(this.newDescription(d));
                this.drinksContainer.appendChild(this.newAddToDrinkBasket(d));
            });
        }
        );

    }

    newH5(text) {
        const h = document.createElement("h5");
        h.appendChild(document.createTextNode(text));
        return h;
    }

    newDescription(d) {
        const p = document.createElement("p");
        p.className = "w3-text-grey";
        const price = "" + d.price;
        p.appendChild(document.createTextNode(d.description + " " + price.slice(0, price.length - 2) + "." + price.slice(price.length - 2, price.length)));

        return p;
    }
    newAddToFoodBasket(d) {

        const a = document.createElement("a");
        a.className = "addToBasket";
        a.onclick = (e) => {
            console.log("Adding to food basket ref: " + d.ref + " " + d.name);
            this.addToDishBasket(d);
        };
        a.appendChild(document.createTextNode("Add"));
        return a;
    }
    addToDishBasket(d) {
        const current = this.dishes.find(e => {
            return e.d.ref === d.ref;
        });
        if (current) {
            current.count++;
        } else {
            this.dishes.push({'d': d, 'count': 1});
        }
    }

    newAddToDrinkBasket(d) {

        const a = document.createElement("a");
        a.className = "addToBasket";
        a.onclick = (e) => {
            console.log("Adding to drink basket ref: " + d.ref + " " + d.name);
            this.addToDrinkBasket(d);
        };
        a.appendChild(document.createTextNode("Add"));
        return a;
    }
    addToDrinkBasket(d) {
        const current = this.drinks.find(e => {
            return e.d.ref === d.ref;
        });
        if (current) {
            current.count++;
        } else {
            this.drinks.push({'d': d, 'count': 1});
        }
    }
    removeFromFoodBasket(c) {

        const a = document.createElement("a");
        a.className = "addToBasket";
        a.onclick = (e) => {
            console.log("Remove from to food basket ref: " + c.d.ref + " " + c.d.name);
            c.count--;
            if (c.count === 0) {
                this.dishes = this.dishes.filter(cc => {
                    return cc.d.ref !== c.d.ref;
                });
            }
            this.fillBasket();
        };
        a.appendChild(document.createTextNode("Del:" + c.count));
        return a;
    }
    removeFromDrinkBasket(c) {

        const a = document.createElement("a");
        a.className = "addToBasket";
        a.onclick = (e) => {
            console.log("Remove from to drink basket ref: " + c.d.ref + " " + c.d.name);
            c.count--;
            if (c.count === 0) {
                this.drinks = this.drinks.filter(cc => {
                    return cc.d.ref !== c.d.ref;
                });
            }
            this.fillBasket();
        };
        a.appendChild(document.createTextNode("Del:" + c.count));
        return a;
    }

    fillBasket() {
        this.clearOrderContainer();
        this.dishes.forEach((c) => {
            this.orderContainer.appendChild(this.newH5(c.d.name));
            this.orderContainer.appendChild(this.newDescription(c.d));
            this.orderContainer.appendChild(this.removeFromFoodBasket(c));

        });
        this.drinks.forEach((c) => {
            this.orderContainer.appendChild(this.newH5(c.d.name));
            this.orderContainer.appendChild(this.newDescription(c.d));
            this.orderContainer.appendChild(this.removeFromDrinkBasket(c));
        });
    }
    openBasket() {
        this.fillBasket();
        document.getElementById('order').style.display = 'block';
    }
    clearOrderContainer() {
        while (this.orderContainer.firstChild)
            this.orderContainer.removeChild(this.orderContainer.firstChild);

    }

}

const view = new IndexView();
view.goHome();



