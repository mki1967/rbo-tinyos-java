for ((x=100; x<164; x++));do java MyXfig 6 ${x:1:2} > out-6-${x:1:2}.fig ;done
for ((x=100; x<164; x++)); do fig2dev -L pdf  out-6-${x:1:2}.fig >out-6-${x:1:2}.pdf; done
pdftk out-6-??.pdf cat output out-6.pdf
