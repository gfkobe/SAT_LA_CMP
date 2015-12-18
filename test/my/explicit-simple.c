int main() 
{
	int a, b = 1, c;
	c = -1;
	if (b > 0) 
	{
		if (b > c) {
			a = 1;		
		} else {
			a = 0;
		}
	} else {
	if (b < c) {
			a = 1;		
} else {
	a = 0;
}
}
if (a == 0)
	goto ERROR;
	return 0;
ERROR:
	return 1;
}
