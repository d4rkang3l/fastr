/*
 * This material is distributed under the GNU General Public License
 * Version 2. You may review the terms of this license at
 * http://www.gnu.org/licenses/gpl-2.0.html
 * 
 * Copyright (c) 2014, Purdue University
 * Copyright (c) 2014, Oracle and/or its affiliates
 * All rights reserved.
 */
package com.oracle.truffle.r.test.testrgen;

import org.junit.*;

import com.oracle.truffle.r.test.*;

public class TestrGenBuiltinacos extends TestBase {

    @Test
    public void testacos1() {
        assertEval("argv <- list(c(0.999950000416665, 0.999800006666578, 0.999550033748988, 0.999200106660978, 0.998750260394966, 0.998200539935204, 0.99755100025328, 0.996801706302619, 0.995952733011994, 0.995004165278026, 0.993956097956697, 0.992808635853866, 0.991561893714788, 0.990215996212637, 0.988771077936042, 0.987227283375627, 0.985584766909561, 0.983843692788121, 0.98200423511727, 0.980066577841242, 0.978030914724148, 0.975897449330606, 0.973666395005375, 0.97133797485203, 0.968912421710645, 0.966389978134513, 0.963770896365891, 0.961055438310771, 0.958243875512697, 0.955336489125606, 0.952333569885713, 0.949235418082441, 0.946042343528387, 0.942754665528346, 0.939372712847379, 0.935896823677935, 0.932327345606034, 0.92866463557651, 0.924909059857313, 0.921060994002885, 0.917120822816605, 0.913088940312308, 0.908965749674885, 0.904751663219963, 0.900447102352677, 0.896052497525525, 0.891568288195329, 0.886994922779284, 0.882332858610121, 0.877582561890373, 0.872744507645751, 0.86781917967765, 0.862807070514761, 0.857708681363824, 0.852524522059506, 0.847255111013416, 0.841900975162269, 0.836462649915187, 0.830940679100164, 0.825335614909678, 0.819648017845479, 0.813878456662534, 0.808027508312152, 0.802095757884293, 0.796083798549056, 0.789992231497365, 0.783821665880849, 0.777572718750928, 0.771246014997107, 0.764842187284488, 0.758361875990508, 0.751805729140895, 0.74517440234487, 0.738468558729588, 0.731688868873821, 0.724836010740905, 0.717910669610943, 0.710913538012277, 0.703845315652236, 0.696706709347165, 0.689498432951747, 0.682221207287614, 0.674875760071267, 0.667462825841308, 0.659983145884982, 0.652437468164052, 0.644826547240001, 0.63715114419858, 0.629412026573697, 0.621609968270664, 0.613745749488812, 0.605820156643463, 0.597833982287298, 0.589788025031098, 0.581683089463884, 0.573519986072457, 0.565299531160354, 0.557022546766217, 0.548689860581588));acos(argv[[1]]);");
    }

    @Test
    public void testacos2() {
        assertEval("argv <- list(1.54308063481524+0i);acos(argv[[1]]);");
    }

    @Test
    public void testacos3() {
        assertEval("argv <- list(c(-0.7104065636993+1i, 0.25688370915653+1i, -0.24669187846237+1i, -0.34754259939773+1i, -0.95161856726502+1i, -0.04502772480892+1i, -0.78490446945708+1i, -1.66794193658814+1i, -0.38022652028776+1i, 0.91899660906077+1i, -0.57534696260839+1i, 0.60796432222503+1i, -1.61788270828916+1i, -0.05556196552454+1i, 0.51940720394346+1i, 0.30115336216671+1i, 0.10567619414894+1i, -0.64070600830538+1i, -0.84970434603358+1i, -1.02412879060491+1i, 0.11764659710013+1i, -0.9474746141848+1i, -0.49055744370067+1i, -0.25609219219825+1i, 1.84386200523221+1i, -0.65194990169546+1i, 0.23538657228486+1i, 0.07796084956371+1i, -0.96185663413013+1i, -0.0713080861236+1i, 1.44455085842335+1i, 0.45150405307921+1i, 0.04123292199294+1i, -0.42249683233962+1i, -2.05324722154052+1i, 1.13133721341418+1i, -1.46064007092482+1i, 0.73994751087733+1i, 1.90910356921748+1i, -1.4438931609718+1i, 0.70178433537471+1i, -0.26219748940247+1i, -1.57214415914549+1i, -1.51466765378175+1i, -1.60153617357459+1i, -0.5309065221703+1i, -1.4617555849959+1i, 0.68791677297583+1i, 2.10010894052567+1i, -1.28703047603518+1i, 0.78773884747518+1i, 0.76904224100091+1i, 0.33220257895012+1i, -1.00837660827701+1i, -0.11945260663066+1i, -0.28039533517025+1i, 0.56298953322048+1i, -0.37243875610383+1i, 0.97697338668562+1i, -0.37458085776701+1i, 1.05271146557933+1i, -1.04917700666607+1i, -1.26015524475811+1i, 3.2410399349424+1i, -0.41685758816043+1i, 0.29822759154072+1i, 0.63656967403385+1i, -0.48378062570874+1i, 0.51686204431361+1i, 0.36896452738509+1i, -0.21538050764169+1i, 0.06529303352532+1i, -0.03406725373846+1i, 2.12845189901618+1i, -0.74133609627283+1i, -1.09599626707466+1i, 0.03778839917108+1i, 0.31048074944314+1i, 0.43652347891018+1i, -0.45836533271111+1i, -1.06332613397119+1i, 1.26318517608949+1i, -0.34965038795355+1i, -0.86551286265337+1i, -0.2362795689411+1i, -0.19717589434855+1i, 1.10992028971364+1i, 0.0847372921972+1i, 0.75405378518452+1i, -0.49929201717226+1i, 0.2144453095816+1i, -0.32468591149083+1i, 0.09458352817357+1i, -0.89536335797754+1i, -1.31080153332797+1i, 1.99721338474797+1i, 0.60070882367242+1i, -1.25127136162494+1i, -0.61116591668042+1i, -1.18548008459731+1i));acos(argv[[1]]);");
    }

    @Test
    public void testacos4() {
        assertEval("argv <- list(structure(numeric(0), .Dim = c(0L, 0L)));acos(argv[[1]]);");
    }
}