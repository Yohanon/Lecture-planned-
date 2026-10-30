package com.example.data.local

object ArjunaDataInitializers {

    fun getDefaultChapters(): List<ChapterEntity> {
        val chapters = mutableListOf<ChapterEntity>()
        var order = 1

        // PHYSICS CHAPTERS (17 chapters)
        val physicsList = listOf(
            Pair("Units and Measurements", 8),
            Pair("Mathematical Tools", 14),
            Pair("Motion in a Straight Line", 12),
            Pair("Scalars and Vectors", 6),
            Pair("Motion in a Plane", 10),
            Pair("Laws of Motion + Friction", 15),
            Pair("Circular Motion", 6),
            Pair("Work, Energy and Power", 11),
            Pair("Centre of Mass & System of Particles", 9),
            Pair("Rotational Motion", 14),
            Pair("Gravitation", 8),
            Pair("Mechanical Properties of Solids", 6),
            Pair("Mechanical Properties of Fluids", 9),
            Pair("Thermal Properties of Matter", 7),
            Pair("Kinetic Theory & Thermodynamics", 12),
            Pair("Simple Harmonic Motion", 9),
            Pair("Waves", 10)
        )

        physicsList.forEach { (name, lectures) ->
            val id = "phy_" + name.lowercase().replace(Regex("[^a-z0-9]"), "_")
            chapters.add(
                ChapterEntity(
                    id = id,
                    subject = "PHYSICS",
                    name = name,
                    orderIndex = order++,
                    totalLectures = lectures,
                    attendedLectures = 0
                )
            )
        }

        // CHEMISTRY CHAPTERS (16 chapters)
        val chemistryList = listOf(
            Pair("Some Basic Concepts of Chemistry", 12),
            Pair("Structure of Atom", 15),
            Pair("Classification of Elements and Periodicity in Properties", 8),
            Pair("Chemical Bonding and Molecular Structure", 16),
            Pair("States of Matter", 9),
            Pair("Thermodynamics & Thermochemistry", 14),
            Pair("Chemical Equilibrium", 8),
            Pair("Ionic Equilibrium", 10),
            Pair("Redox Reactions", 6),
            Pair("Organic Chemistry - IUPAC Nomenclature", 8),
            Pair("Organic Chemistry - Isomerism", 8),
            Pair("Organic Chemistry - General Organic Chemistry (GOC)", 16),
            Pair("Hydrocarbons", 12),
            Pair("Purification and Analysis of Organic Compound", 5),
            Pair("Environmental Chemistry", 4),
            Pair("P-block Elements (Group 13 and 14)", 10)
        )

        chemistryList.forEach { (name, lectures) ->
            val id = "chem_" + name.lowercase().replace(Regex("[^a-z0-9]"), "_")
            chapters.add(
                ChapterEntity(
                    id = id,
                    subject = "CHEMISTRY",
                    name = name,
                    orderIndex = order++,
                    totalLectures = lectures,
                    attendedLectures = 0
                )
            )
        }

        // MATHEMATICS CHAPTERS (19 chapters)
        val mathList = listOf(
            Pair("Basic Mathematics", 10),
            Pair("Sets", 6),
            Pair("Quadratic Equations", 10),
            Pair("Trigonometric Functions", 12),
            Pair("Trigonometric Equation", 6),
            Pair("Complex Number - I", 8),
            Pair("Sequence and Series", 10),
            Pair("Binomial Theorem", 10),
            Pair("Permutations and Combinations", 12),
            Pair("Relation Function", 8),
            Pair("Straight Lines", 12),
            Pair("Circles", 10),
            Pair("Conic Sections: Parabola", 8),
            Pair("Conic Sections: Ellipse", 7),
            Pair("Conic Sections: Hyperbola", 6),
            Pair("Complex Number - II", 8),
            Pair("Limits and Derivatives", 12),
            Pair("Statistics", 5),
            Pair("Probability", 9)
        )

        mathList.forEach { (name, lectures) ->
            val id = "math_" + name.lowercase().replace(Regex("[^a-z0-9]"), "_")
            chapters.add(
                ChapterEntity(
                    id = id,
                    subject = "MATHEMATICS",
                    name = name,
                    orderIndex = order++,
                    totalLectures = lectures,
                    attendedLectures = 0
                )
            )
        }

        return chapters
    }

    fun getDefaultTests(): List<TestEntity> {
        val tests = mutableListOf<TestEntity>()

        tests.add(
            TestEntity(
                id = "test_1",
                name = "Short Test -1",
                dateStr = "Sunday, June 21, 2026",
                testType = "Part Test",
                testPattern = "Short Test",
                orderIndex = 1,
                chaptersListCsv = "Units and Measurements, Some Basic Concepts of Chemistry, Basic Mathematics"
            )
        )

        tests.add(
            TestEntity(
                id = "test_2",
                name = "JEE Main-1",
                dateStr = "Sunday, July 5, 2026",
                testType = "Part Test",
                testPattern = "JEE Main",
                orderIndex = 2,
                chaptersListCsv = "Units and Measurements, Mathematical Tools, Some Basic Concepts of Chemistry, Basic Mathematics"
            )
        )

        tests.add(
            TestEntity(
                id = "test_3",
                name = "JEE Main-2",
                dateStr = "Sunday, July 19, 2026",
                testType = "Part Test",
                testPattern = "JEE Main",
                orderIndex = 3,
                chaptersListCsv = "Mathematical Tools, Motion in a Straight Line, Some Basic Concepts of Chemistry, Structure of Atom, Basic Mathematics, Sets"
            )
        )

        tests.add(
            TestEntity(
                id = "test_4",
                name = "JEE Advanced-1",
                dateStr = "Sunday, August 2, 2026",
                testType = "Part Test",
                testPattern = "JEE Advanced",
                orderIndex = 4,
                chaptersListCsv = "Units and Measurements, Mathematical Tools, Motion in a Straight Line, Some Basic Concepts of Chemistry, Structure of Atom, Basic Mathematics, Sets"
            )
        )

        tests.add(
            TestEntity(
                id = "test_5",
                name = "Board Pattern Test-1",
                dateStr = "Sunday, August 16, 2026",
                testType = "Part Test",
                testPattern = "Board Pattern Test",
                orderIndex = 5,
                chaptersListCsv = "Units and Measurements, Mathematical Tools, Motion in a Straight Line, Some Basic Concepts of Chemistry, Structure of Atom, Basic Mathematics, Sets"
            )
        )

        tests.add(
            TestEntity(
                id = "test_6",
                name = "AITS-1",
                dateStr = "Sunday, August 23, 2026",
                testType = "Part Test",
                testPattern = "JEE Main",
                orderIndex = 6,
                chaptersListCsv = "Mathematical Tools, Structure of Atom, Basic Mathematics"
            )
        )

        tests.add(
            TestEntity(
                id = "test_7",
                name = "AITS-2",
                dateStr = "Sunday, August 30, 2026",
                testType = "Part Test",
                testPattern = "JEE Advanced",
                orderIndex = 7,
                chaptersListCsv = "Mathematical Tools, Structure of Atom, Basic Mathematics"
            )
        )

        tests.add(
            TestEntity(
                id = "test_8",
                name = "JEE Main-3",
                dateStr = "Sunday, September 13, 2026",
                testType = "Part Test",
                testPattern = "JEE Main",
                orderIndex = 8,
                chaptersListCsv = "Motion in a Straight Line, Scalars and Vectors, Structure of Atom, Classification of Elements and Periodicity in Properties, Quadratic Equations, Trigonometric Functions"
            )
        )

        tests.add(
            TestEntity(
                id = "test_9",
                name = "JEE Main-4",
                dateStr = "Sunday, September 27, 2026",
                testType = "Part Test",
                testPattern = "JEE Main",
                orderIndex = 9,
                chaptersListCsv = "Motion in a Plane, Classification of Elements and Periodicity in Properties, Trigonometric Equation"
            )
        )

        tests.add(
            TestEntity(
                id = "test_10",
                name = "AITS-3",
                dateStr = "Sunday, October 4, 2026",
                testType = "Part Test",
                testPattern = "JEE Main",
                orderIndex = 10,
                chaptersListCsv = "Units and Measurements, Motion in a Straight Line, Some Basic Concepts of Chemistry, Classification of Elements and Periodicity in Properties, Sets, Quadratic Equations"
            )
        )

        tests.add(
            TestEntity(
                id = "test_11",
                name = "AITS-4",
                dateStr = "Sunday, October 11, 2026",
                testType = "Part Test",
                testPattern = "JEE Advanced",
                orderIndex = 11,
                chaptersListCsv = "Units and Measurements, Motion in a Straight Line, Some Basic Concepts of Chemistry, Classification of Elements and Periodicity in Properties, Sets, Quadratic Equations"
            )
        )

        tests.add(
            TestEntity(
                id = "test_12",
                name = "JEE Main-5",
                dateStr = "Sunday, October 25, 2026",
                testType = "Part Test",
                testPattern = "JEE Main",
                orderIndex = 12,
                chaptersListCsv = "Laws of Motion + Friction, Circular Motion, Chemical Bonding and Molecular Structure, States of Matter, Sequence and Series, Complex Number - I"
            )
        )

        tests.add(
            TestEntity(
                id = "test_13",
                name = "JEE Advanced-2",
                dateStr = "Sunday, November 22, 2026",
                testType = "Part Test",
                testPattern = "JEE Advanced",
                orderIndex = 13,
                chaptersListCsv = "Motion in a Straight Line, Scalars and Vectors, Motion in a Plane, Laws of Motion + Friction, Circular Motion, Classification of Elements and Periodicity in Properties, Chemical Bonding and Molecular Structure, States of Matter, Quadratic Equations, Trigonometric Functions, Trigonometric Equation, Sequence and Series, Complex Number - I"
            )
        )

        tests.add(
            TestEntity(
                id = "test_14",
                name = "AITS-5",
                dateStr = "Sunday, November 29, 2026",
                testType = "Part Test",
                testPattern = "JEE Main",
                orderIndex = 14,
                chaptersListCsv = "Motion in a Plane, Laws of Motion + Friction, Work, Energy and Power, Chemical Bonding and Molecular Structure, States of Matter, Thermodynamics & Thermochemistry, Trigonometric Functions, Sequence and Series, Binomial Theorem, Permutations and Combinations"
            )
        )

        tests.add(
            TestEntity(
                id = "test_15",
                name = "AITS-6",
                dateStr = "Sunday, December 6, 2026",
                testType = "Part Test",
                testPattern = "JEE Advanced",
                orderIndex = 15,
                chaptersListCsv = "Motion in a Plane, Laws of Motion + Friction, Work, Energy and Power, Chemical Bonding and Molecular Structure, States of Matter, Thermodynamics & Thermochemistry, Trigonometric Functions, Sequence and Series, Binomial Theorem, Permutations and Combinations"
            )
        )

        tests.add(
            TestEntity(
                id = "test_16",
                name = "Board Pattern Test-2",
                dateStr = "Sunday, December 20, 2026",
                testType = "Part Test",
                testPattern = "Board Pattern Test",
                orderIndex = 16,
                chaptersListCsv = "Units and Measurements, Mathematical Tools, Motion in a Straight Line, Scalars and Vectors, Motion in a Plane, Laws of Motion + Friction, Circular Motion, Work, Energy and Power, Some Basic Concepts of Chemistry, Structure of Atom, Classification of Elements and Periodicity in Properties, Chemical Bonding and Molecular Structure, States of Matter, Thermodynamics & Thermochemistry, Basic Mathematics, Sets, Quadratic Equations, Trigonometric Functions, Trigonometric Equation, Complex Number - I, Sequence and Series, Binomial Theorem, Permutations and Combinations"
            )
        )

        tests.add(
            TestEntity(
                id = "test_17",
                name = "JEE Main-6",
                dateStr = "Sunday, January 3, 2027",
                testType = "Part Test",
                testPattern = "JEE Main",
                orderIndex = 17,
                chaptersListCsv = "Work, Energy and Power, Centre of Mass & System of Particles, Rotational Motion, Mechanical Properties of Solids, Thermodynamics & Thermochemistry, Redox Reactions, Chemical Equilibrium, Ionic Equilibrium, Organic Chemistry - IUPAC Nomenclature, Permutations and Combinations, Binomial Theorem, Relation Function, Straight Lines"
            )
        )

        tests.add(
            TestEntity(
                id = "test_18",
                name = "AITS-8",
                dateStr = "Sunday, January 17, 2027",
                testType = "Part Test",
                testPattern = "JEE Advanced",
                orderIndex = 18,
                chaptersListCsv = "Centre of Mass & System of Particles, Rotational Motion, Gravitation, Mechanical Properties of Solids, Chemical Equilibrium, Ionic Equilibrium, Redox Reactions, Organic Chemistry - IUPAC Nomenclature, Straight Lines, Circles"
            )
        )

        tests.add(
            TestEntity(
                id = "test_19",
                name = "Board Pattern Test-3",
                dateStr = "Sunday, January 31, 2027",
                testType = "Full Test",
                testPattern = "Board Pattern Test",
                orderIndex = 19,
                chaptersListCsv = "Units and Measurements, Motion in a Straight Line, Motion in a Plane, Laws of Motion + Friction, Work, Energy and Power, Rotational Motion, Gravitation, Mechanical Properties of Solids, Mechanical Properties of Fluids, Thermal Properties of Matter, Structure of Atom, Chemical Bonding and Molecular Structure, Thermodynamics & Thermochemistry, Equilibrium, Organic Chemistry - General Organic Chemistry (GOC), Hydrocarbons, Sets, Relations and Functions, Trigonometric Functions, Permutations and Combinations, Binomial Theorem, Sequence and Series, Straight Lines, Conic Sections, Limits and Derivatives, Statistics, Probability"
            )
        )

        tests.add(
            TestEntity(
                id = "test_20",
                name = "AITS-9",
                dateStr = "Sunday, February 7, 2027",
                testType = "Part Test",
                testPattern = "JEE Main",
                orderIndex = 20,
                chaptersListCsv = "Mathematical Tools, Units and Measurements, Motion in a Straight Line, Motion in a Plane, Laws of Motion + Friction, Work, Energy and Power, Centre of Mass & System of Particles, Rotational Motion, Gravitation, Mechanical Properties of Solids, Mechanical Properties of Fluids, Thermal Properties of Matter, Kinetic Theory & Thermodynamics, Structure of Atom, Some Basic Concepts of Chemistry, Classification of Elements and Periodicity in Properties, Chemical Bonding and Molecular Structure, States of Matter, Thermodynamics & Thermochemistry, Chemical Equilibrium, Ionic Equilibrium, Redox Reactions, Organic Chemistry - IUPAC Nomenclature, Organic Chemistry - Isomerism, Organic Chemistry - General Organic Chemistry (GOC), Basic Mathematics, Sets, Quadratic Equations, Trigonometric Functions, Sequence and Series, Binomial Theorem, Permutations and Combinations, Probability, Straight Lines, Circles, Limits and Derivatives, Conic Sections: Parabola, Conic Sections: Ellipse, Conic Sections: Hyperbola"
            )
        )

        tests.add(
            TestEntity(
                id = "test_21",
                name = "JEE Main-7",
                dateStr = "Sunday, February 21, 2027",
                testType = "Part Test",
                testPattern = "JEE Main",
                orderIndex = 21,
                chaptersListCsv = "Thermal Properties of Matter, Kinetic Theory & Thermodynamics, Gravitation, Mechanical Properties of Fluids, Simple Harmonic Motion, Waves, Organic Chemistry - General Organic Chemistry (GOC), Organic Chemistry - Isomerism, Hydrocarbons, Purification and Analysis of Organic Compound, Environmental Chemistry, P-block Elements (Group 13 and 14), Circles, Conic Sections: Parabola, Conic Sections: Ellipse, Conic Sections: Hyperbola, Complex Number - II, Limits and Derivatives, Statistics, Probability"
            )
        )

        tests.add(
            TestEntity(
                id = "test_22",
                name = "JEE Main-8",
                dateStr = "Sunday, February 28, 2027",
                testType = "Full Test",
                testPattern = "JEE Main",
                orderIndex = 22,
                chaptersListCsv = "Units and Measurements, Motion in a Straight Line, Laws of Motion + Friction, Work, Energy and Power, Rotational Motion, Gravitation, Mechanical Properties of Fluids, Thermodynamics & Thermochemistry, Simple Harmonic Motion, Waves, Structure of Atom, Chemical Bonding and Molecular Structure, Thermodynamics & Thermochemistry, Chemical Equilibrium, Organic Chemistry - General Organic Chemistry (GOC), Hydrocarbons, Quadratic Equations, Sequence and Series, Binomial Theorem, Straight Lines, Circles, Limits and Derivatives, Probability"
            )
        )

        tests.add(
            TestEntity(
                id = "test_23",
                name = "JEE Advanced-3",
                dateStr = "Sunday, March 7, 2027",
                testType = "Full Test",
                testPattern = "JEE Advanced",
                orderIndex = 23,
                chaptersListCsv = "Units and Measurements, Motion in a Straight Line, Laws of Motion + Friction, Work, Energy and Power, Rotational Motion, Gravitation, Mechanical Properties of Solids, Mechanical Properties of Fluids, Thermal Properties of Matter, Kinetic Theory & Thermodynamics, Simple Harmonic Motion, Waves, Structure of Atom, Chemical Bonding and Molecular Structure, Chemical Equilibrium, Ionic Equilibrium, Organic Chemistry - General Organic Chemistry (GOC), Hydrocarbons, Quadratic Equations, Trigonometric Functions, Sequence and Series, Permutations and Combinations, Straight Lines, Circles, Conic Sections: Parabola, Conic Sections: Ellipse, Limits and Derivatives, Probability"
            )
        )

        tests.add(
            TestEntity(
                id = "test_24",
                name = "AITS-10",
                dateStr = "Sunday, March 14, 2027",
                testType = "Full Test",
                testPattern = "JEE Main",
                orderIndex = 24,
                chaptersListCsv = "Units and Measurements, Motion in a Straight Line, Motion in a Plane, Laws of Motion + Friction, Work, Energy and Power, Rotational Motion, Gravitation, Simple Harmonic Motion, Waves, Some Basic Concepts of Chemistry, Structure of Atom, Chemical Bonding and Molecular Structure, Thermodynamics & Thermochemistry, Equilibrium, Organic Chemistry - General Organic Chemistry (GOC), Hydrocarbons, Basic Mathematics, Sets, Quadratic Equations, Trigonometric Functions, Sequence and Series, Binomial Theorem, Permutations and Combinations, Straight Lines, Circles, Probability"
            )
        )

        tests.add(
            TestEntity(
                id = "test_25",
                name = "AITS-11",
                dateStr = "Sunday, March 21, 2027",
                testType = "Full Test",
                testPattern = "JEE Advanced",
                orderIndex = 25,
                chaptersListCsv = "Units and Measurements, Motion in a Straight Line, Motion in a Plane, Laws of Motion + Friction, Work, Energy and Power, Centre of Mass & System of Particles, Rotational Motion, Gravitation, Mechanical Properties of Solids, Mechanical Properties of Fluids, Thermal Properties of Matter, Kinetic Theory & Thermodynamics, Simple Harmonic Motion, Waves, Structure of Atom, Chemical Bonding and Molecular Structure, Thermodynamics & Thermochemistry, Chemical Equilibrium, Ionic Equilibrium, Organic Chemistry - General Organic Chemistry (GOC), Hydrocarbons, Quadratic Equations, Trigonometric Functions, Sequence and Series, Binomial Theorem, Permutations and Combinations, Straight Lines, Circles, Conic Sections: Parabola, Conic Sections: Ellipse, Conic Sections: Hyperbola, Limits and Derivatives, Probability"
            )
        )

        return tests
    }
}
