package qtool.stats.corr

case class Option(in: String = null,
                  out: String = "out.tsv",
                  seperator: String = "\t",
                  method: String = "pearson")
