int main() 
{
	

	int a = 0;
int *p, *q;
p = &a;
q = p;
*p = 0;
*q = 1;
if (a == 0)
	goto ERROR;
	return 0;
ERROR:
	return 1;
}
