for ((x=100; x<132; x++));do java MyXfig 5 ${x:1:2} > out-5-${x:1:2}.fig ;done
for ((x=100; x<132; x++)); do fig2dev -L pdf  out-5-${x:1:2}.fig >out-5-${x:1:2}.pdf; done
pdftk out-5-??.pdf cat output out-5.pdf
