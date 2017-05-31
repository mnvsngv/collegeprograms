; 	Program to perform 32-bit addition in 80386 processor
;	Author: Manav Sanghavi	Author Link: https://www.facebook.com/manav.sanghavi
;	www.pracspedia.com

.model small ;				Use the 'small' model
.386p ;						Use the 80386 processor

.data ;						Start of data segment
	n1 dd 1234567Ah ;		n1 = 1234567AH
	n2 dd 11111111h ;		n2 = 11111111H
	count db 08h ;			Initialize 'count' as 8 (hex) bits

.code ;						Start of code
.startup ;					Initializes various registers
	mov ax, @data ;			Initialize data segment and store address in AX
	mov ds, ax ;			Store data segment address value in DS
	mov eax, n1 ;			Move 32 bit number n1 into EAX
	mov ebx, n2 ;			Move 32 bit number n2 into EBX
	add eax, ebx ;			Add 32 bit registers EAX and EBX
	call disp ;				Call 'disp' procedure to display answer
	mov ah, 4ch ;			DOS terminate program function
	int 21h ;				Terminate the program
	
	disp proc near ;		Procedure to display the answer on console
	mov cl, count ;			Initialize the CL register for looping
	up:
		rol eax, 04h ;		Rotate EAX 4 bits to the left
		mov bx, ax ;		Save content of AX into BX
		and al, 0Fh ;		Set upper 4 bits of AL as 0
		cmp al, 0Ah ;		Compare AL to see if it is hex or not
		jnge notgreater ;	Jump if not greater (i.e. if it is not hex)
			add al, 07h ;	Add 7 if it is hex digit to get correct ASCII value
		notgreater:
		add al, 30h ;		Convert into ASCII value
		mov dl, al ;		Move contents of AL into DL
		mov ah, 02h ;		Command to display contents of DL on console
		int 21h ;			Display contents of DL
		mov ax, bx ;		Restore original content of AX
		loop up ;			Decrements CL and goes back to 'up' if CL!=0
	ret ;					Return to calling function
	endp disp ;				End of procedure
end ;						End of program
.exit ;						End of program

; OUTPUT:
; 2345678B