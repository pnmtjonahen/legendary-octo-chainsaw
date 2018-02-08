/* global fetch */

class IndexView {
    constructor() {
        this.eatContainer = document.querySelector("#Eat");
        this.drinksContainer = document.querySelector("#Drinks");
        this.orderContainer = document.querySelector("#Basket");
        this.drinks = [];
        this.dishes = [];
        this.orderInfo = undefined;

    }

    goHome() {
        fetch("http://localhost:8080/api/menu").then(res => res.json()).then(menu => {
            menu.dishes.forEach((d) => {
                this.eatContainer.appendChild(this.newH5(d.name));
                this.eatContainer.appendChild(this.newDescription(d));
                this.eatContainer.appendChild(this.anchorAddToFoodCart(d));

            });
            menu.drinks.forEach((d) => {
                this.drinksContainer.appendChild(this.newH5(d.name));
                this.drinksContainer.appendChild(this.newDescription(d));
                this.drinksContainer.appendChild(this.anchorddToDrinkCart(d));
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
        p.appendChild(document.createTextNode(d.description + " " + this.markupPrice(price)));

        return p;
    }

    markupPrice(price) {
        return price.slice(0, price.length - 2) + "." + price.slice(price.length - 2, price.length);
    }

    anchorAddToFoodCart(d) {

        const a = document.createElement("a");
        a.className = "addToBasket";
        a.onclick = (e) => {
            const current = this.dishes.find(e => {
                return e.d.ref === d.ref;
            });
            if (current) {
                current.count++;
            } else {
                this.dishes.push({'d': d, 'count': 1});
            }
        };
        const it = document.createElement("i");
        it.className = "w3-xxlarge fa fa-cart-plus";
        a.appendChild(it);
        return a;
    }

    anchorddToDrinkCart(d) {

        const a = document.createElement("a");
        a.className = "addToBasket";
        a.onclick = (e) => {
            const current = this.drinks.find(e => {
                return e.d.ref === d.ref;
            });
            if (current) {
                current.count++;
            } else {
                this.drinks.push({'d': d, 'count': 1});
            }
        };
        const it = document.createElement("i");
        it.className = "w3-xxlarge fa fa-cart-plus";
        a.appendChild(it);
        return a;
    }
    anchorRemoveFromFoodCart(c) {

        const a = document.createElement("a");
        a.onclick = (e) => {
            console.log("Remove from to food basket ref: " + c.d.ref + " " + c.d.name);
            c.count--;
            if (c.count <= 0) {
                this.dishes = this.dishes.filter(cc => {
                    return cc.d.ref !== c.d.ref;
                });
            }
            this.fillBasket();
        };
        a.appendChild(document.createTextNode(c.count + " : "));
        const it = document.createElement("i");
        it.className = "w3-xxlarge fa fa-trash";
        a.appendChild(it);
        return a;
    }
    anchorRemoveFromDrinkCart(c) {

        const a = document.createElement("a");
        a.onclick = (e) => {
            console.log("Remove from to drink basket ref: " + c.d.ref + " " + c.d.name);
            c.count--;
            if (c.count <= 0) {
                this.drinks = this.drinks.filter(cc => {
                    return cc.d.ref !== c.d.ref;
                });
            }
            this.fillBasket();
        };
        a.appendChild(document.createTextNode(c.count + " : "));
        const it = document.createElement("i");
        it.className = "w3-xxlarge fa fa-trash";
        a.appendChild(it);
        return a;
    }

    fillBasket() {
        this.clearOrderContainer();
        if (this.dishes.length > 0 || this.drinks.length > 0) {
            const table = document.createElement("table");
            table.className = "order-table";
            var total = 0;
            this.dishes.forEach((c) => {
                table.appendChild(this.appendOrderRow(c.d.name, c.d.price * c.count, this.anchorRemoveFromFoodCart(c)));
                total += c.d.price * c.count;
            });
            this.drinks.forEach((c) => {
                table.appendChild(this.appendOrderRow(c.d.name, c.d.price * c.count, this.anchorRemoveFromDrinkCart(c)));
                total += c.d.price * c.count;
            });
            table.appendChild(this.appendOrderRow("Total", total, null));

            this.orderContainer.appendChild(table);
            this.orderContainer.appendChild(this.sendOrderButton());
        } else {
            this.orderContainer.appendChild(this.newH5("Cart is empty. Add food or dinks to the card.."));
        }
    }
    sendOrderButton() {
        const p = document.createElement("p");
        const button = document.createElement("button");
        button.className = "w3-button";
        button.type = "submit";
        button.onclick = (e) => {

            const order = [];
            this.dishes.forEach((c) => {
                order.push({"ref": c.d.ref, "quantity": c.count, "type": "DISH"});
            });
            this.drinks.forEach((c) => {
                order.push({"ref": c.d.ref, "quantity": c.count, "type": "DRINK"});
            });
            fetch("http://localhost:8080/api/order/",
                    {
                        headers: {
                            'Accept': 'application/json',
                            'Content-Type': 'application/json'
                        },
                        method: "POST",
                        body: JSON.stringify(order)
                    })
                    .then(res => res.json())
                    .then(res => {
                        console.log(res);
                        this.orderInfo = res;
                        this.closeCart();
                    })
                    .catch(res => {
                        console.log(res);
                    });

        };
        button.appendChild(document.createTextNode("SEND ORDER"));
        p.appendChild(button);
        return p;
    }

    appendOrderRow(name, price, c) {
        const row = document.createElement("tr");
        var cell = document.createElement("td");
        cell.className = "first-cell";
        cell.appendChild(this.newH5(name));
        row.appendChild(cell);
        cell = document.createElement("td");
        cell.className = "price-cell";
        cell.appendChild(this.newH5(this.markupPrice("" + price)));
        row.appendChild(cell);
        cell = document.createElement("td");
        cell.className = "action-cell";
        if (c) {
            cell.appendChild(c);
        }
        row.appendChild(cell);
        return row;

    }
    openCart() {
        this.fillBasket();
        document.getElementById('order').style.display = 'block';
    }
    closeCart() {
        document.getElementById('order').style.display = 'none';
    }
    clearOrderContainer() {
        while (this.orderContainer.firstChild)
            this.orderContainer.removeChild(this.orderContainer.firstChild);

    }

}

const view = new IndexView();
view.goHome();



