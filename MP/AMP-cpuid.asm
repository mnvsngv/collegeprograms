; 	Using CPUID instruction
;	Author: Manav Sanghavi	Author Link: https://www.facebook.com/manav.sanghavi
;	www.pracspedia.com

.model small ;					Use the 'small' model
.586p ;							Use the 586 processor

.data ;							Start of data segment
	a dd ? ;					Declare 'a' as double
	b dd ? ;					Declare 'b' as double
	c dd ? ;					Declare 'c' as double
	d dd ? ;					Declare 'd' as double
	msg db "EAX: ", '$' ;		Create string variable to display on screen

.code ;							Start of code
.startup ;						Initializes various registers
	mov eax, 00000000h ;		Clear the EAX register before using CPUID
	cpuid ;						CPUID instruction
	mov a, eax ;				Store contents of EAX in memory
	mov b, ebx ;				Store contents of EBX in memory
	mov c, ecx ;				Store contents of ECX in memory
	mov d, edx ;				Store contents of EDX in memory
	
	mov dx, offset msg ;
	mov ah, 09h ;
	int 21h ;					Code to display string
	
	mov eax, a ;				Store the value to be displayed 'a' in EAX
	call dispdig ;				Call procedure to display 32-bit number in EAX
	call newline ;				Call procedure for sending cursor to new line
	mov eax, b ;				Store the value to be displayed 'b' in EAX
	call dispch ;				Call procedure to display characters in EAX
	mov eax, d ;				Store the value to be displayed 'd' in EAX
	call dispch ;				Call procedure to display characters in EAX
	mov eax, c ;				Store the value to be displayed 'c' in EAX
	call dispch ;				Call procedure to display characters in EAX
	mov ah, 4ch ;				DOS terminate program function
	int 21h ;					Terminate the program
	
	dispdig proc near ;			Procedure to display 32-bit number on console
		mov cx, 0008h ;			Initialize counter register CX
		updig:
			rol eax, 04h ;		Rotate EAX 4 bits to the left
			mov bx, ax ;		Save content of AX into BX
			and al, 0Fh ;		Set upper 4 bits of AL as 0
			cmp al, 0Ah ;		Compare AL to see if it is hex or not
			jnge notgreater ;	Jump if not greater (i.e. if it is not hex)
				add al, 07h ;	Add 7 if it is hex digit to get ASCII value
			notgreater:
			add al, 30h ;		Convert into ASCII value
			mov dl, al ;		Move contents of DL into AL
			mov ah, 02h ;		Command to display contents of DL on console
			int 21h ;			Display contents of DL on console
			mov ax, bx ;		Restore original contents of AX
			loop updig ;		Decrements CX and goes back to 'updig' if CX!=0
		ret ;					Return to calling function
	endp dispdig ;				End of procedure
	
	dispch proc near ;			Procedure to display characters on console
		mov dl, al ;			Move contents of DL into AL
		mov ebx, eax ;			Save content of EAX into EBX
		mov ah, 02h ;			Command to display contents of DL on console
		int 21h ;				Display contents of DL on console
		mov eax, ebx ;			Restore original content of EAX
		mov cx, 0003h ;			Initialize counter register CX
		upch:
			ror eax, 08h ;		Rotate EAX 8 bits to the right
			mov ebx, eax ;		Save content of EAX in EBX
			mov dl, al ;		Move content of AL into DL
			mov ah, 02h ;		Command to display content of DL on console
			int 21h ;			Display contents of DL on console
			mov eax, ebx ;		Restore original content of EAX
			loop upch ;			Decrements CX and goes back to 'upch' if CX!=0
		ret ;					Return to calling function
	endp dispch ;				End procedure
	
	newline proc near ;			Procedure to insert a new line
		mov dl, 13 ;			CR
		mov ah, 02h ;			Move cursor to start of current line
		int 21h ;				Transfer control to DOS to execute the CR
		mov dl, 10 ;			LF
		mov ah, 02h ;			Move cursor to next line
		int 21h ;				Transfer control to DOS to execute the LF
		ret ;					Return to calling function
	endp newline ;				End of procedure
end ;							End of program
.exit ;							End of program

; OUTPUT
; EAX: 00000001
; GenuineIntel