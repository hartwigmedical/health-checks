package com.hartwig.healthchecks.common.predicate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.hartwig.healthchecks.common.predicate.VCFHeaderLinePredicate;

public class VCFHeaderLinePredicateTest {

    private static final String PASS_DATA_LINE = "2\t212812060\trs839540\t%s\t%s\t.\tPASS\tAC=1;AF=0.333;AN=3;"
                    + "ANN=C|sequence_feature|LOW|ERBB4|ENSG00000178568|topological_domain:Extracellular|"
                    + "ENST00000342788|protein_coding||c.421+95A>G||||||,C|sequence_feature|LOW|ERBB4|ENSG00000178568|"
                    + "topological_domain:Extracellular|ENST00000436443|protein_coding||c.421+95A>G||||||,C|"
                    + "intron_variant|MODIFIER|ERBB4|ENSG00000178568|transcript|ENST00000342788|protein_coding|3/27|"
                    + "c.421+95A>G||||||,C|intron_variant|MODIFIER|ERBB4|ENSG00000178568|transcript|ENST00000436443|"
                    + "protein_coding|3/26|c.421+95A>G||||||,C|intron_variant|MODIFIER|ERBB4|ENSG00000178568|"
                    + "transcript|ENST00000402597|protein_coding|3/27|c.421+95A>G||||||,C|intron_variant|"
                    + "MODIFIER|ERBB4|ENSG00000178568|transcript|ENST00000484594|"
                    + "retained_intron|3/19|n.473+95A>G||||||,"
                    + "C|intron_variant|MODIFIER|ERBB4|ENSG00000178568|transcript|ENST00000260943|protein_coding|"
                    + "3/18|c.418+95A>G||||||WARNING_TRANSCRIPT_INCOMPLETE,C|intron_variant|MODIFIER|ERBB4|"
                    + "ENSG00000178568|transcript|ENST00000484474|processed_transcript|2/4|n.338+95A>G||||||,"
                    + "C|intron_variant|MODIFIER|ERBB4|ENSG00000178568|transcript|ENST00000435846|protein_coding|"
                    + "3/4|c.244+95A>G||||||WARNING_TRANSCRIPT_INCOMPLETE;DB;SOMATIC;VT=SNP;set=mutect"
                    + "\tGT:AD:BQ:DP:FA:SS\t./.\t0:25,0:.:25:0.00:0\t./.\t0/1:5,13:40:18:0.722:2\t./. ./. ./. ./.";

    private static final String DOT_DATA_LINE = "2\t212812060\trs839540\t%s\t%s\t.\t.\t" + "AC=1;AF=0.333;AN=3;"
                    + "ANN=C|sequence_feature|LOW|ERBB4|ENSG00000178568|topological_domain:Extracellular|"
                    + "ENST00000342788|protein_coding||c.421+95A>G||||||,C|sequence_feature|LOW|ERBB4|ENSG00000178568|"
                    + "topological_domain:Extracellular|ENST00000436443|protein_coding||c.421+95A>G||||||,C|"
                    + "intron_variant|MODIFIER|ERBB4|ENSG00000178568|transcript|ENST00000342788|protein_coding|3/27|"
                    + "c.421+95A>G||||||,C|intron_variant|MODIFIER|ERBB4|ENSG00000178568|transcript|ENST00000436443|"
                    + "protein_coding|3/26|c.421+95A>G||||||,C|intron_variant|MODIFIER|ERBB4|ENSG00000178568|"
                    + "transcript|ENST00000402597|protein_coding|3/27|c.421+95A>G||||||,C|intron_variant|"
                    + "MODIFIER|ERBB4|ENSG00000178568|transcript|ENST00000484594|"
                    + "retained_intron|3/19|n.473+95A>G||||||,"
                    + "C|intron_variant|MODIFIER|ERBB4|ENSG00000178568|transcript|ENST00000260943|protein_coding|"
                    + "3/18|c.418+95A>G||||||WARNING_TRANSCRIPT_INCOMPLETE,C|intron_variant|MODIFIER|ERBB4|"
                    + "ENSG00000178568|transcript|ENST00000484474|processed_transcript|2/4|n.338+95A>G||||||,"
                    + "C|intron_variant|MODIFIER|ERBB4|ENSG00000178568|transcript|ENST00000435846|protein_coding|"
                    + "3/4|c.244+95A>G||||||WARNING_TRANSCRIPT_INCOMPLETE;DB;SOMATIC;VT=SNP;set=mutect"
                    + "\tGT:AD:BQ:DP:FA:SS\t./.\t0:25,0:.:25:0.00:0\t./.\t0/1:5,13:40:18:0.722:2\t./. ./. ./. ./.";

    private static final String OTHER_DATA_LINE = "2\t212812060\trs839540\tT\tC\t.\tbla\t" + "AC=1;AF=0.333;AN=3;"
                    + "ANN=C|sequence_feature|LOW|ERBB4|ENSG00000178568|topological_domain:Extracellular|"
                    + "ENST00000342788|protein_coding||c.421+95A>G||||||,C|sequence_feature|LOW|ERBB4|ENSG00000178568|"
                    + "topological_domain:Extracellular|ENST00000436443|protein_coding||c.421+95A>G||||||,C|"
                    + "intron_variant|MODIFIER|ERBB4|ENSG00000178568|transcript|ENST00000342788|protein_coding|3/27|"
                    + "c.421+95A>G||||||,C|intron_variant|MODIFIER|ERBB4|ENSG00000178568|transcript|ENST00000436443|"
                    + "protein_coding|3/26|c.421+95A>G||||||,C|intron_variant|MODIFIER|ERBB4|ENSG00000178568|"
                    + "transcript|ENST00000402597|protein_coding|3/27|c.421+95A>G||||||,C|intron_variant|"
                    + "MODIFIER|ERBB4|ENSG00000178568|transcript|ENST00000484594|"
                    + "retained_intron|3/19|n.473+95A>G||||||,"
                    + "C|intron_variant|MODIFIER|ERBB4|ENSG00000178568|transcript|ENST00000260943|protein_coding|"
                    + "3/18|c.418+95A>G||||||WARNING_TRANSCRIPT_INCOMPLETE,C|intron_variant|MODIFIER|ERBB4|"
                    + "ENSG00000178568|transcript|ENST00000484474|processed_transcript|2/4|n.338+95A>G||||||,"
                    + "C|intron_variant|MODIFIER|ERBB4|ENSG00000178568|transcript|ENST00000435846|protein_coding|"
                    + "3/4|c.244+95A>G||||||WARNING_TRANSCRIPT_INCOMPLETE;DB;SOMATIC;VT=SNP;set=mutect"
                    + "\tGT:AD:BQ:DP:FA:SS\t./.\t0:25,0:.:25:0.00:0\t./.\t0/1:5,13:40:18:0.722:2\t./. ./. ./. ./.";

    private static final String FILLING_LINE = "##FILTER=<ID=str10,Description="
                    + "\"Less than 10% or more than 90% of variant supporting reads on one strand\">";

    private static final String HEADER_LINE = "#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\tFORMAT"
                    + "\tCPCT12345678R\tCPCT12345678T";

    private VCFHeaderLinePredicate predicate;

    @Before
    public void setUp() {
        predicate = new VCFHeaderLinePredicate();
    }

    @Test
    public void checkHeaderLine() {
        assertTrue(predicate.test(HEADER_LINE));
    }

    @Test
    public void checkPassLine() {
        assertFalse(predicate.test(PASS_DATA_LINE));
    }

    @Test
    public void checkDotLine() {
        assertFalse(predicate.test(DOT_DATA_LINE));
    }

    @Test
    public void checkHashLine() {
        assertFalse(predicate.test(FILLING_LINE));
    }

    @Test
    public void checkOtherLine() {
        assertFalse(predicate.test(OTHER_DATA_LINE));
    }
}
