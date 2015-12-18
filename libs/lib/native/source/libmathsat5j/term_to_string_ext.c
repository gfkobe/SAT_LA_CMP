#define term_to_string_ext(func, func_escaped) \
  DEFINE_FUNC(string, func_escaped) WITH_FOUR_ARGS(jenv, jterm, string, int) \
  ENV_ARG(1) \
  TERM_ARG(2) \
  STRING_ARG(3) \
  SIMPLE_ARG(int, 4) \
  CALL4(char *, func) \
  STRING_RETURN


term_to_string_ext(to_smtlib2_ext, 1to_1smtlib2_1ext)
