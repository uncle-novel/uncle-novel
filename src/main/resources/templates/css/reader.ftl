.root{
-fx-base: ${bgColor};
-fx-background: -fx-base;
text-color: ladder(-fx-base, rgba(255, 255, 255, 0.87) 49%, rgba(0, 0, 0, 0.87) 50%);
border-color: ladder(-fx-base, rgba(255, 255, 255, 0.3) 49%, rgba(0, 0, 0, 0.3) 50%);
hover-bg-color: #009688;
hover-text-color:  ladder(hover-bg-color, rgba(255, 255, 255, 0.87) 49%, rgba(0, 0, 0, 0.87) 50%);
selected-bg-color:  linear-gradient(to left, rgb(31, 212, 174), rgb(30, 205, 148));
selected-text-color:  ladder(selected-bg-color, rgba(255, 255, 255, 0.87) 49%, rgba(0, 0, 0, 0.87) 50%);
}

