; 	Program to display a character and digit on console screen in MUP 80386
;	Author: Manav Sanghavi	Author Link: https://www.facebook.com/manav.sanghavi
;	www.pracspedia.com

.model tiny ;			Use the 'tiny' model
.386p ;					Use the 386 processor

.code ;					Start of code
.startup ;				Initializes various registers
	mov al, 'a' ;		Move character 'a' into AL
	mov dl, al ;		Move content of AL into DL
	mov ah, 02h ;		Command to display contents of DL on console
	int 21h ;			Display 'a' on console
	call newline ;		Call method to move cursor to next line
	mov al, 09h ;		Move number 9 into AL
	add al, 30h ;		Convert the number into ASCII value
	mov dl, al ;		Move contents of AL into DL
	mov ah, 02h ;		Command to display contents of DL on console
	int 21h ;			Display 9 on console
	mov ah, 4ch ;		DOS terminate program function
	int 21h ;			Terminate the program
	
	newline proc near ;	Create a procedure to insert a new line
	mov dl, 13 ;		'13' in DL moves cursor to start of current line
	;					This is the "Carriage Return" control character (CR)
	
	mov ah, 02h ;		Command to execute DL's contents
	int 21h ;			Move cursor to start of the current line
	mov dl, 10 ;		'10' in DL moves cursor to next line
	;					This is the "Line Feed" control character (LF)
	
	mov ah, 02h ;		Command to execute DL's contents
	int 21h ;			Move cursor to next line
	ret ;				Return to calling function
	endp newline ;		End of procedure
end ;					End of program
.exit ;					End of program

; OUTPUT:
; a
; 9