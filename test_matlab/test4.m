A=[1 2 3 4 5 6 7 8 9 10; 11 12 13 14 15 16 17 18 19 20; 21 22 23 24 25 26 27 28 29 30]
A(:, 0:2:8)
A(1, 0:2:8)
A(:, 9)
B=A(0:2,0:2)

C=[5 4 8; 4 4 7; 2 1 1]
b=A(:,3)
C\b